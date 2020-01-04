package com.senderman.miniroulette.commands

import com.senderman.miniroulette.RouletteBotHandler
import com.senderman.miniroulette.Services
import com.senderman.neblib.CommandExecutor
import org.telegram.telegrambots.meta.api.objects.Message

class GetCoins(private val handler: RouletteBotHandler) : CommandExecutor {
    override val command: String
        get() = "/getcoins"
    override val desc: String
        get() = "получить монетки (раз в день при балансе < 300"

    override fun execute(message: Message) {
        val chatId = message.chatId
        val messageId = message.messageId
        val userId = message.from.id
        if (Services.db.getCoins(userId) >= 300){
            handler.sendMessage(chatId, "У вас и так много денег!", messageId)
            return
        }
        if (message.date - Services.db.getLastRequestDate(userId) < 86400){
            handler.sendMessage(chatId, "Просить монетки можно только раз в день!")
            return
        }

        Services.db.setLastRequestDate(userId, message.date)
        Services.db.addCoins(userId, 300)
        handler.sendMessage(chatId, "Вы получили 300 монет!", messageId)
    }
}