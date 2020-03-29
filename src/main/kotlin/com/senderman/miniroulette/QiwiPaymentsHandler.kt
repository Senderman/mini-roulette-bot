package com.senderman.miniroulette

import com.qiwi.billpayments.sdk.client.BillPaymentClientFactory
import com.qiwi.billpayments.sdk.model.BillStatus.*
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
    val daysWaitingFor = 15L
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
            ZonedDateTime.now().plusDays(daysWaitingFor),
            null,
            successUrl
        )
        return client.createBill(billInfo).payUrl
    }

    fun runBillChecking() = thread {
        while (true) {
            forLoop@ for (bill in Services.db.getWaitingBills()) {

                when (client.getBillInfo(bill.billId).status.value!!) {
                    WAITING -> continue@forLoop

                    EXPIRED -> {
                        try {
                            handler.sendMessage(
                                bill.userId.toLong(),
                                "Ожидание платежа за ${bill.coins} монет истекло!"
                            )
                        } catch (ignored: Exception) {
                        }
                        Services.db.removeBill(bill.billId)
                    }
                    REJECTED -> {
                        try {
                            handler.sendMessage(
                                bill.userId.toLong(),
                                "Платеж за ${bill.coins} монет был отклонен!"
                            )
                        } catch (ignored: Exception) {
                        }
                        Services.db.removeBill(bill.billId)
                    }
                    PAID -> {
                        Services.db.addCoins(bill.userId, bill.coins)
                        try {
                            handler.sendMessage(
                                bill.userId.toLong(),
                                "Платеж за ${bill.coins} монет выполнен! Приятной игры!"
                            )
                            handler.sendMessage(
                                Services.botConfig.mainAdmin.toLong(),
                                "Поступил донат на ${bill.coins / 60} рублей от " +
                                        "<a href=\"tg://user?id=${bill.userId}\">этого</a> юзера!"
                            )
                        } catch (ignored: Exception) {
                        }
                        Services.db.removeBill(bill.billId)
                    }
                }
                Thread.sleep(TimeUnit.SECONDS.toMillis(2))
            }
            Thread.sleep(TimeUnit.MINUTES.toMillis(checkInterval))
        }
    }
}
