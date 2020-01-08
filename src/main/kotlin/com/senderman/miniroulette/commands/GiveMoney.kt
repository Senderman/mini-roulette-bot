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
        if (!message.isReply) return

        val chatId = message.chatId
        val amount = try {
            message.text.split("\\s+".toRegex())[1].toInt()
        } catch (e: NumberFormatException) {
            return
        } catch (e: IndexOutOfBoundsException){
            return
        }
        if (amount < 1) return

        val sender = message.from
        val receiver = message.replyToMessage.from
        if (receiver.bot){
            handler.sendMessage(chatId, "Но это же бот!", message.messageId)
            return
        }
        if (Services.db.getCoins(sender.id) - 10 < amount) {
            handler.sendMessage(
                chatId, "У вас недостаточно денег (на балансе должно остаться минимум 10 монет!",
                message.messageId
            )
            return
        }
        Services.db.takeCoins(sender.id, amount)
        Services.db.addCoins(receiver.id, amount)
        handler.sendMessage(chatId, "✅ Успешно!", message.messageId)
    }
}