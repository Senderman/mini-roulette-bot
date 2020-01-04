package com.senderman.miniroulette.commands

import com.senderman.miniroulette.RouletteBotHandler
import com.senderman.miniroulette.Services
import com.senderman.neblib.CommandExecutor
import org.telegram.telegrambots.meta.api.objects.Message

class Balance(private val handler: RouletteBotHandler): CommandExecutor {
    override val command: String
        get() = "/balance"
    override val desc: String
        get() = "ваш баланс"

    override fun execute(message: Message) {
        val coins = Services.db.getCoins(message.from.id)
        handler.sendMessage(message.chatId, "\uD83D\uDCB0 Ваш баланс: $coins")
    }
}