package com.senderman.miniroulette

import com.senderman.miniroulette.gameobjects.Game
import org.telegram.telegrambots.meta.api.objects.Message

interface MainHandler {
    fun addGame(game: Game)
    fun removeGame(game: Game)
    fun getGame(chatId: Long): Game?
    fun containsGame(chatId: Long): Boolean
    fun sendMessage(chatId: Long, text: String, replyToMessageId: Int? = null): Message
    fun deleteMessage(chatId: Long, messageId: Int)
}