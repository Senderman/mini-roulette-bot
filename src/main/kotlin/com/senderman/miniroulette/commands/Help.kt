package com.senderman.miniroulette.commands

import com.senderman.miniroulette.RouletteBotHandler
import com.senderman.miniroulette.Services
import com.senderman.neblib.CommandExecutor
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class Help(
    private val handler: RouletteBotHandler,
    private val commandExecutors: Map<String, CommandExecutor>
) : CommandExecutor {
    override val command: String
        get() = "/rhelp"
    override val desc: String
        get() = "помощь"
    override val showInHelp: Boolean
        get() = false

    override fun execute(message: Message) {
        val help = StringBuilder("Команды:\n")
        for ((command, executor) in commandExecutors.filterValues { it.showInHelp }) {
            help.appendln("$command - ${executor.desc}")
        }
        help.insert(0, Services.botConfig.help + "\n\n")
        try {
            val sm = SendMessage(message.chatId, help.toString())
            handler.execute(sm)
            if (!message.isUserMessage){
                handler.sendMessage(message.chatId, "✅ Помощь отправлена вам в лс", message.messageId)
            }
        } catch (e: TelegramApiException){
            if (!message.isUserMessage){
                handler.sendMessage(message.chatId, "Сначала напишите боту в лс!", message.messageId)
            }
        }
    }

}