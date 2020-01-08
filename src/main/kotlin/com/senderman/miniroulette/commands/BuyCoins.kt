package com.senderman.miniroulette.commands

import com.senderman.miniroulette.QiwiPaymentsHandler
import com.senderman.miniroulette.RouletteBotHandler
import com.senderman.miniroulette.Services
import com.senderman.miniroulette.WaitingBill
import com.senderman.neblib.CommandExecutor
import com.senderman.neblib.TgUser
import org.telegram.telegrambots.meta.api.objects.Message
import java.util.*

class BuyCoins(private val handler: RouletteBotHandler) : CommandExecutor {
    override val command: String
        get() = "/buy"
    override val desc: String
        get() = "купить монетки. Формат /buy 3000. От $minAmount до $maxAmount. Курс RUB-COINS - 1:60"
    private val qiwi = QiwiPaymentsHandler(handler)

    private val minAmount = 300
    private val maxAmount = 100000

    init {
        qiwi.runBillChecking()
    }

    override fun execute(message: Message) {
        val chatId = message.chatId
        if (!message.isUserMessage) {
            handler.sendMessage(chatId, "Команду можно использовать только в лс!", message.messageId)
            return
        }

        val amount = try {
            message.text.split("\\s+".toRegex())[1].toInt()
        } catch (e: NumberFormatException) {
            handler.sendMessage(chatId, "Неверный формат!")
            return
        } catch (e: IndexOutOfBoundsException) {
            return
        }
        if (amount < minAmount || amount > maxAmount) {
            handler.sendMessage(chatId, "Неверное значение!")
            return
        }

        val price = convertToRUB(amount).toDouble()
        val billId = UUID.randomUUID().toString()
        val waitingBill = WaitingBill(message.from.id, amount, billId)
        handler.sendMessage(chatId, "Генерация счета...")
        val paymentFormUrl = qiwi.getPaymentFormUrl(TgUser(message.from), amount, price, billId)
        Services.db.addWaitingBill(waitingBill)
        val text = """
            В течение ${qiwi.waitingFor} дней вам нужно перейти по ссылке $paymentFormUrl и оплатить счет.
            Проверка оплаты выполняется каждые ${qiwi.checkInterval} минут.
            При изменении статуса платежа вы будете уведомлены в лс
        """.trimIndent()
        handler.sendMessage(chatId, text)
    }

    private fun convertToRUB(coins: Int) = coins / 60
}