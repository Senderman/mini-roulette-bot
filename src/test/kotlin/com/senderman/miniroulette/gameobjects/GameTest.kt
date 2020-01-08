package com.senderman.miniroulette.gameobjects

import com.senderman.miniroulette.DBService
import com.senderman.miniroulette.MainHandler
import com.senderman.miniroulette.Services
import com.senderman.miniroulette.WaitingBill
import org.junit.jupiter.api.Test

internal class GameTest {
    @Test
    fun runTest() {
        val handler = TestHandler()
        Services.db = TestDBService()
        val game = Game(handler, 0)
        handler.addGame(game)
        game.runTimer()
        game.addBet(123, "Юлька", "20 1-3", 1)
        game.addBet(123, "Юлька", "300 1-3", 1)
        game.addBet(111, "Пасюк", "228 0", 1)
        while (handler.containsGame(0))
            Thread.sleep(500)
        assert(true)
    }
}

internal class TestDBService : DBService {

    override fun getTop10(): LinkedHashMap<Int, Int>  = LinkedHashMap()
    override fun getWaitingBills(): Set<WaitingBill> {
    }

    override fun addWaitingBill(bill: WaitingBill) {
    }

    override fun removeBill(billId: String) {
    }

    override fun getCoins(userId: Int): Int = 5000

    override fun addCoins(userId: Int, amount: Int) {
    }

    override fun takeCoins(userId: Int, amount: Int) {
    }

    override fun getLastRequestDate(userId: Int): Int = 0

    override fun setLastRequestDate(userId: Int, date: Int) {
    }
}

internal class TestHandler : MainHandler {

    private val games = HashMap<Long, Game>()

    override fun addGame(game: Game) {
        games[game.chatId] = game
    }

    override fun removeGame(game: Game) {
        games.remove(game.chatId)
    }

    override fun getGame(chatId: Long): Game? = games[chatId]

    override fun containsGame(chatId: Long): Boolean = chatId in games

    override fun sendMessage(chatId: Long, text: String, replyToMessageId: Int?) = println(text)

}