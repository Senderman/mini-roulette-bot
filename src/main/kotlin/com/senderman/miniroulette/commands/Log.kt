package com.senderman.miniroulette.commands

import com.senderman.miniroulette.RouletteBotHandler
import com.senderman.miniroulette.Services
import com.senderman.neblib.CommandExecutor
import org.telegram.telegrambots.meta.api.objects.Message

class Log(private val handler: RouletteBotHandler) : CommandExecutor {
    override val command: String
        get() = "/rlog"
    override val desc: String
        get() = "лог последних результатов в чате"

    override fun execute(message: Message) {
        val chatId = message.chatId
        val log = Services.db.getLog(chatId)
        if (log == null) {
            handler.sendMessage(chatId, "В этом чате пока еще не было игр!")
            return
        }
        handler.sendMessage(chatId, "\uD83D\uDCC3<b>Лог результатов</b> (ниже - новее)\n$log")
    }
}