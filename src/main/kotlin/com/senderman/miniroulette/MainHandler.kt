package com.senderman.miniroulette

import com.senderman.miniroulette.gameobjects.Game

interface MainHandler {
    fun addGame(game: Game)
    fun removeGame(game: Game)
    fun getGame(chatId: Long): Game?
    fun containsGame(chatId:Long): Boolean
    fun sendMessage(chatId: Long, text:String, replyToMessageId: Int? = null)
}