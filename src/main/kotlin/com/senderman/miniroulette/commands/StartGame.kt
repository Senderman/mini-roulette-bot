package com.senderman.miniroulette.commands

import com.senderman.miniroulette.RouletteBotHandler
import com.senderman.miniroulette.gameobjects.Game
import com.senderman.neblib.CommandExecutor
import org.telegram.telegrambots.meta.api.objects.Message

class StartGame(private val handler: RouletteBotHandler) : CommandExecutor {
    override val command: String
        get() = "/rourun"
    override val desc: String
        get() = "Начать игру"

    override fun execute(message: Message) {
        val chatId = message.chatId
        if (handler.containsGame(chatId)) {
            handler.sendMessage(chatId, "Игра уже идет здесь!")
            return
        }
        val game = Game(handler, chatId)
        handler.addGame(game)
        game.runTimer()
    }
}