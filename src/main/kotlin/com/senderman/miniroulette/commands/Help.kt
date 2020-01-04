package com.senderman.miniroulette.commands

import com.senderman.miniroulette.RouletteBotHandler
import com.senderman.miniroulette.Services
import com.senderman.neblib.CommandExecutor
import org.telegram.telegrambots.meta.api.objects.Message

class Help(private val handler: RouletteBotHandler) : CommandExecutor {
    override val command: String
        get() = "/rhelp"
    override val desc: String
        get() = "помощь"
    override val showInHelp: Boolean
        get() = false

    override fun execute(message: Message) = handler.sendMessage(message.chatId, Services.botConfig.help)
}