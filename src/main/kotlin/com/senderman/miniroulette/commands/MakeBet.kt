package com.senderman.miniroulette.commands

import com.senderman.miniroulette.RouletteBotHandler
import com.senderman.neblib.CommandExecutor
import com.senderman.neblib.TgUser
import org.telegram.telegrambots.meta.api.objects.Message

class MakeBet(private val handler: RouletteBotHandler) : CommandExecutor {
    override val command: String
        get() = "/bet"
    override val desc: String
        get() = "сделать ставку"

    override fun execute(message: Message) {
        val chatId = message.chatId
        val text = message.text.replace("@${handler.botToken}".toRegex(), "")
        handler.getGame(chatId)?.let {
            it.addBet(TgUser(message.from), text, message.messageId)
            return
        }
        handler.sendMessage(chatId, "Игра еще не запущена!")
    }
}