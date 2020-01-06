package com.senderman.miniroulette.commands

import com.annimon.tgbotsmodule.api.methods.Methods
import com.senderman.miniroulette.RouletteBotHandler
import com.senderman.miniroulette.Services
import com.senderman.miniroulette.UserStats
import com.senderman.neblib.CommandExecutor
import org.telegram.telegrambots.meta.api.objects.Message

class Top(private val handler: RouletteBotHandler) : CommandExecutor {
    override val command: String
        get() = "/rtop"
    override val desc: String
        get() = "топ богачей"

    override fun execute(message: Message) {
        val top = Services.db.getTop10()
        val text = StringBuilder("\uD83D\uDCB0 <b>Топ-10 богачей:\n\n</b>")
        var counter = 1
        for ((userId, coins) in top) {
            val name = Methods.getChatMember(userId.toLong(), userId).call(handler).user.firstName ?: "Без имени"
            val player = UserStats(userId, name, coins)
            text.append(counter).append(": ")
            if (message.isUserMessage) text.append(player.link) else text.append(player.name)
            text.appendln(" - ${player.coins}$")
            counter++
        }
        handler.sendMessage(message.chatId, text.toString())
    }
}