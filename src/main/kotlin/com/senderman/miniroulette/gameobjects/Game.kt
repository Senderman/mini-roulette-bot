package com.senderman.miniroulette.gameobjects

import com.senderman.miniroulette.MainHandler
import com.senderman.miniroulette.Services
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

class Game(private val handler: MainHandler, val chatId: Long) {

    private val maxTime = 30
    private var waitingForBets = true
    private var timer: AtomicInteger = AtomicInteger(0)
    private val players = HashMap<Int, Player>() // id-player
    private var currentCell = -1
    private val messagesToDelete = HashSet<Int>()

    fun addBet(userId: Int, name: String, text: String, messageId: Int) {
        messagesToDelete.add(messageId)
        if (!waitingForBets) {
            handler.sendMessage(chatId, "Слишком поздно!", messageId)
            return
        }

        val amount = try {
            text.trim().split("\\s+".toRegex())[0].toInt()
        } catch (e: NumberFormatException) {
            handler.sendMessage(chatId, "Неверный формат!")
            return
        }
        if (amount < 2) {
            handler.sendMessage(chatId, "Ставка должна быть не меньше 2 монет!", messageId)
            return
        }

        if (Services.db.getCoins(userId) < amount) {
            handler.sendMessage(chatId, "У вас недостаточно денег!", messageId)
            return
        }

        val target = text.trim().toLowerCase().replace("^\\d+\\s+".toRegex(), "")
        val bet = try {
            Bet.createBet(amount, target)
        } catch (e: InvalidBetCommandException) {
            handler.sendMessage(chatId, "Неверный формат!")
            return
        } catch (e: InvalidBetRangeException) {
            handler.sendMessage(chatId, "Неверный диапазон!")
            return
        }

        if (userId in players)
            players[userId]!!.bets.add(bet)
        else {
            val player = Player(userId, name)
            player.bets.add(bet)
            players[userId] = player
        }

        Services.db.takeCoins(userId, amount)
        timer.set(0)
        messagesToDelete.add(handler.sendMessage(chatId, "Ставка принята!", messageId).messageId)
    }

    fun runTimer() {
        handler.sendMessage(chatId, "\uD83C\uDFB0 Делайте ваши ставки\n\n$fieldString")
        thread {
            while (timer.get() < maxTime) {
                timer.incrementAndGet()
                Thread.sleep(1000)
            }
            handler.sendMessage(chatId, "❇️ Ставки кончились, ставок больше нет")
            spin()
        }
    }


    private fun spin() {
        waitingForBets = false
        currentCell = ThreadLocalRandom.current().nextInt(0, 13)
        if (currentCell == 0)
            processZero()
        else
            processNonZero()

        val logEntry: String = currentCell.toString() + when {
            currentCell == 0 -> "💚"
            currentCell.isEven() -> "\uD83D\uDDA4"
            else -> "❤"
        }
        val log = (Services.db.getLog(chatId) ?: "")
            .lines()
            .takeLast(9)
            .joinToString(separator = "\n", truncated = "")
            .trim()
        val finalLog = "$log\n$logEntry"
        Services.db.setLog(chatId, finalLog)

        messagesToDelete.forEach { handler.deleteMessage(chatId, it) }
        handler.removeGame(this)
    }

    private fun processZero() {
        val text = StringBuilder("\uD83C\uDFB2 Итоги:\n")
        text.append("\uD83D\uDC9A 0\n\n")
        for (player in players.values) {
            text.appendln("<b>${player.name}:</b>")
            var profit = 0
            var delta = 0

            for (bet in player.bets) {
                if (bet.isWin(currentCell)) {
                    profit = bet.pay + bet.amount
                    delta += bet.pay
                    text.appendln("\uD83D\uDE0E +${bet.pay} (${bet.amount} на ${bet.stringTarget})")
                } else {
                    profit += bet.amount / 2
                    delta -= bet.amount / 2
                    text.appendln("\uD83D\uDE14 -${bet.amount / 2} (${bet.amount} на ${bet.stringTarget})")
                }
            }

            Services.db.addCoins(player.id, profit)
            text.appendln(formatDelta(delta)).appendln()
        }
        handler.sendMessage(chatId, text.trim().toString())
    }

    private fun processNonZero() {
        val text = StringBuilder("\uD83C\uDFB2 Итоги:\n")
        val colorEmoji = if (currentCell.isEven()) "\uD83D\uDDA4" else "❤️"
        text.append("$colorEmoji $currentCell\n\n")
        for (player in players.values) {
            text.appendln("<b>${player.name}:</b>")
            var profit = 0
            var delta = 0

            for (bet in player.bets) {
                if (bet.isWin(currentCell)) {
                    profit += bet.amount + bet.pay
                    delta += bet.pay
                    text.appendln("\uD83D\uDE0E +${bet.pay} (${bet.amount} на ${bet.stringTarget})")
                } else {
                    delta -= bet.amount
                    text.appendln("\uD83D\uDE14 -${bet.amount} (${bet.amount} на ${bet.stringTarget})")
                }
            }

            Services.db.addCoins(player.id, profit)
            text.appendln(formatDelta(delta)).appendln()
        }
        handler.sendMessage(chatId, text.trim().toString())
    }

    private fun Int.isEven() = this % 2 == 0
    private fun formatDelta(delta: Int) = if (delta > 0) "\uD83D\uDCC8: +$delta" else "\uD83D\uDCC9: $delta"

    companion object {
        val fieldString = """
            0💚 
            1❤️ 2🖤 3❤️ 4🖤 5❤️ 6🖤
            7❤️ 8🖤 9❤️ 10🖤 11❤️ ️12🖤
        """.trimIndent()
    }

}