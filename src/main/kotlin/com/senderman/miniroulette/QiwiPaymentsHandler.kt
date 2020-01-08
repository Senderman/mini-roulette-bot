package com.senderman.miniroulette

import com.qiwi.billpayments.sdk.client.BillPaymentClientFactory
import com.qiwi.billpayments.sdk.model.BillStatus
import com.qiwi.billpayments.sdk.model.MoneyAmount
import com.qiwi.billpayments.sdk.model.`in`.CreateBillInfo
import com.senderman.neblib.TgUser
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

    fun getPaymentFormUrl(user: TgUser, coins: Int, price: Double, billId: String): String {
        val moneyAmount = MoneyAmount(
            BigDecimal.valueOf(price),
            Currency.getInstance("RUB")
        )
        val successUrl = "https://qiwi.com"
        val billInfo = CreateBillInfo(
            billId,
            moneyAmount,
            "Донат в бота от ${user.name} на $coins монеток",
            ZonedDateTime.now().plusDays(15),
            null,
            successUrl
        )
        return client.createBill(billInfo).payUrl
    }

    fun runBillChecking() = thread {
        while (true) {
            forLoop@ for (bill in Services.db.getWaitingBills()) {
                val status = client.getBillInfo(bill.billId).status.value!!
                if (status == BillStatus.WAITING) continue

                when (status) {
                    BillStatus.WAITING -> continue@forLoop

                    BillStatus.EXPIRED -> {
                        handler.sendMessage(
                            bill.userId.toLong(),
                            "Ожидание платежа за ${bill.coins} монет истекло!"
                        )
                        Services.db.removeBill(bill.billId)
                    }
                    BillStatus.REJECTED -> {
                        handler.sendMessage(
                            bill.userId.toLong(),
                            "Платеж за ${bill.coins} монет был отклонен!"
                        )
                        Services.db.removeBill(bill.billId)
                    }
                    BillStatus.PAID -> {
                        Services.db.addCoins(bill.userId, bill.coins)
                        handler.sendMessage(
                            bill.userId.toLong(),
                            "Платеж за ${bill.coins} монет выполнен! Приятной игры!"
                        )
                        Services.db.removeBill(bill.billId)
                    }
                }
                Thread.sleep(TimeUnit.SECONDS.toMillis(2))
            }
            Thread.sleep(TimeUnit.MINUTES.toMillis(checkInterval))
        }
    }
}
