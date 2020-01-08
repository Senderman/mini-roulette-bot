package com.senderman.miniroulette

import com.qiwi.billpayments.sdk.client.BillPaymentClientFactory
import com.qiwi.billpayments.sdk.model.BillStatus
import com.qiwi.billpayments.sdk.model.MoneyAmount
import com.qiwi.billpayments.sdk.model.`in`.CreateBillInfo
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class QiwiPaymentsHandler(private val handler: MainHandler) {
    private val secretKey = System.getenv("qiwisecret")
    private val client = BillPaymentClientFactory.createDefault(secretKey)
    val waitingFor = 15
    val checkInterval = 10L

    /**
     * @param billId - internal id of the bill
     * @param amount - price
     * @return url to qiwi payment form
     */
    fun getPaymentForm(billId: String, amount: Double): String {
        val moneyAmount = MoneyAmount(
            BigDecimal.valueOf(amount),
            Currency.getInstance("RUB")
        )
        val successUrl = "https://qiwi.com"
        val billInfo = CreateBillInfo(
            billId,
            moneyAmount,
            "донат в бота на $amount монеток",
            ZonedDateTime.now().plusDays(15),
            null,
            successUrl
        )
        return client.createBill(billInfo).payUrl
    }

    fun runBillChecking() = thread {
        while (true) {
            Thread.sleep(TimeUnit.MINUTES.toMillis(checkInterval))
            for (bill in Services.db.getWaitingBills()) {
                val status = client.getBillInfo(bill.billId).status.value
                if (status.isWaiting()) continue

                if (status.isExpired()) {
                    handler.sendMessage(
                        bill.userId.toLong(),
                        "Ожидание платежа за ${bill.coins} монет истекло!"
                    )
                    Services.db.removeBill(bill.billId)
                    continue
                }
                if (status.isRejected()) {
                    handler.sendMessage(
                        bill.userId.toLong(),
                        "Платеж за ${bill.coins} монет был отклонен!"
                    )
                    Services.db.removeBill(bill.billId)
                    continue
                }
                if (status.isPaid()) {
                    Services.db.addCoins(bill.userId, bill.coins)
                    handler.sendMessage(
                        bill.userId.toLong(),
                        "Платеж за ${bill.coins} монет выполнен! Приятной игры!"
                    )
                    Services.db.removeBill(bill.billId)
                }
            }
        }
    }.start()

    private fun BillStatus.isPaid() = this.value == "PAID"
    private fun BillStatus.isExpired() = this.value == "EXPIRED"
    private fun BillStatus.isRejected() = this.value == "REJECTED"
    private fun BillStatus.isWaiting() = this.value == "WAITING"
}