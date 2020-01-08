package com.senderman.miniroulette.commands

import com.senderman.miniroulette.RouletteBotHandler
import com.senderman.miniroulette.Services
import com.senderman.neblib.CommandExecutor
import org.telegram.telegrambots.meta.api.objects.Message

class GiveMoney(private val handler: RouletteBotHandler) : CommandExecutor {
    override val command: String
        get() = "/give"
    override val desc: String
        get() = "(reply) дать монетки. формат /give x"

    override fun execute(message: Message) {
        if (!message.isReply)
            return

        val chatId = message.chatId
        val amount = try {
            message.text.split("\\s+".toRegex())[1].toInt()
        } catch (e: NumberFormatException) {
            return
        }
        val sender = message.from.id
        val receiver = message.replyToMessage.from.id
        if (Services.db.getCoins(sender) - 10 < amount) {
            handler.sendMessage(
                chatId, "У вас недостаточно денег (на балансе должно остаться минимум 10 монет!",
                message.messageId
            )
            return
        }
        Services.db.takeCoins(sender, amount)
        Services.db.addCoins(receiver, amount)
        handler.sendMessage(chatId, "✅ Успешно!", message.messageId)
    }
}