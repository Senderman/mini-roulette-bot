package com.senderman.miniroulette.commands

import com.senderman.miniroulette.RouletteBotHandler
import com.senderman.miniroulette.Services
import com.senderman.neblib.CommandExecutor
import org.telegram.telegrambots.meta.api.objects.Message

class GetCoins(private val handler: RouletteBotHandler) : CommandExecutor {
    override val command: String
        get() = "/getcoins"
    override val desc: String
        get() = "получить монетки (раз в день при балансе < 300, в любое время при балансе = 0)"

    override fun execute(message: Message) {
        val chatId = message.chatId
        val messageId = message.messageId
        val userId = message.from.id
        val currentCoins = Services.db.getCoins(userId)
        if (currentCoins >= 300) {
            handler.sendMessage(chatId, "У вас и так много денег!", messageId)
            return
        }
        if (currentCoins > 0 && message.date - Services.db.getLastRequestDate(userId) < 86400) {
            handler.sendMessage(chatId, "Просить монетки при балансе >0 можно только раз в день!")
            return
        }
        val amountToAdd = if (currentCoins == 0) 10 else 300
        if (amountToAdd == 300) {
            Services.db.setLastRequestDate(userId, message.date)
        }
        Services.db.addCoins(userId, amountToAdd)
        handler.sendMessage(chatId, "Вы получили $amountToAdd монет!", messageId)
    }
}