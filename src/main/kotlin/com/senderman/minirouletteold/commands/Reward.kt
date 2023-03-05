package com.senderman.miniroulette.commands

import com.senderman.miniroulette.RouletteBotHandler
import com.senderman.miniroulette.Services
import com.senderman.neblib.CommandExecutor
import org.telegram.telegrambots.meta.api.objects.Message

class Reward(private val handler: RouletteBotHandler) : CommandExecutor {
    override val command: String
        get() = "/reward"
    override val desc: String
        get() = "(reply) наградить пользователя. /reward 2000. Только для админа"
    override val forMainAdmin: Boolean
        get() = true
    override val showInHelp: Boolean
        get() = false

    override fun execute(message: Message) {
        if (!message.isReply) return
        val chatId = message.chatId
        val amount = try {
            message.text.split("\\s+".toRegex())[1].toInt()
        } catch (e: NumberFormatException) {
            handler.sendMessage(chatId, "Неверный формат!", message.messageId)
            return
        } catch (e: IndexOutOfBoundsException) {
            handler.sendMessage(chatId, "Неверный формат!", message.messageId)
            return
        }
        Services.db.addCoins(message.replyToMessage.from.id, amount)
        handler.sendMessage(chatId, "Вам выдали $amount монеток!", message.replyToMessage.messageId)
    }
}