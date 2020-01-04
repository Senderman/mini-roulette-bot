package com.senderman.miniroulette.gameobjects

import com.senderman.miniroulette.MainHandler
import com.senderman.miniroulette.Services
import com.senderman.neblib.TgUser
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

class Game(private val handler: MainHandler, val chatId: Long) {

    private val maxTime = 30
    private var waitingBets = true
    private var timer: AtomicInteger = AtomicInteger(0)
    private val bets = HashSet<Bet>()
    private var currentCell = -1

    fun addBet(player: TgUser, text: String, messageId: Int) {
        if (!waitingBets) {
            handler.sendMessage(chatId, "Слишком поздно!", messageId)
            return
        }

        val amount = try {
            text.trim().split("\\s+".toRegex())[1].toInt()
        } catch (e: NumberFormatException) {
            handler.sendMessage(chatId, "Неверный формат!")
            return
        }
        if (amount < 1){
            handler.sendMessage(chatId, "Ставка должна быть положительной!", messageId)
            return
        }

        if (Services.db.getCoins(player.id) < amount) {
            handler.sendMessage(chatId, "У вас недостаточно денег!", messageId)
            return
        }

        val target = text.trim().replace("/bet\\s+\\d+\\s+на\\s+".toRegex(), "")
        val bet = try {
            Bet.createBet(player, amount, target)
        } catch (e: InvalidBetCommandException) {
            handler.sendMessage(chatId, "Неверный формат!")
            return
        } catch (e: InvalidBetRangeException) {
            handler.sendMessage(chatId, "Неверный диапазон!")
            return
        }

        bets.add(bet)
        Services.db.takeCoins(player.id, amount)
        timer.set(0)
        handler.sendMessage(chatId, "Ставка принята!", messageId)
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
        waitingBets = false
        currentCell = ThreadLocalRandom.current().nextInt(0, 13)
        if (currentCell == 0)
            processZero()
        else
            processNonZero()
        handler.removeGame(this)
    }

    private fun processZero() {
        val text = StringBuilder("\uD83C\uDFB2 Итоги:\n")
        text.append("\uD83D\uDC9A 0\n\n")
        for (bet in bets) {
            val isWinner = when (bet) {
                is Bet.Straight -> bet.target == 0
                is Bet.Split -> bet.first == 0
                is Bet.Trio -> bet.first == 0
                is Bet.Corner -> bet.first == 0
                else -> false
            }
            if (isWinner) {
                Services.db.addCoins(bet.player.id, bet.pay + bet.amount)
                text.appendln("\uD83D\uDE0E ${bet.player.name} получает ${bet.pay}")
            } else {
                Services.db.addCoins(bet.player.id, bet.amount / 2)
                text.appendln("\uD83D\uDE14 ${bet.player.name} возвращает ${bet.amount / 2}")
            }
        }
        handler.sendMessage(chatId, text.toString())
    }

    private fun processNonZero() {
        val text = StringBuilder("\uD83C\uDFB2 Итоги:\n")
        val colorEmoji = if (currentCell.isEven()) "\uD83D\uDDA4" else "❤️"
        text.append("$colorEmoji $currentCell\n\n")
        for (bet in bets) {
            val isWinner = when (bet) {
                is Bet.Straight -> bet.target == currentCell
                is Bet.Split -> bet.first == currentCell || bet.second == currentCell
                is Bet.Trio -> bet.first <= currentCell && bet.last >= currentCell
                is Bet.Corner -> bet.first <= currentCell && bet.last >= currentCell
                is Bet.Color -> when (bet.color) {
                    COLOR.BLACK -> currentCell.isEven()
                    COLOR.RED -> currentCell.isOdd()
                }
            }
            if (isWinner) {
                Services.db.addCoins(bet.player.id, bet.pay + bet.amount)
                text.appendln("\uD83D\uDE0E ${bet.player.name} получает ${bet.pay}")
            } else {
                text.appendln("\uD83D\uDE14 ${bet.player.name} теряет ${bet.amount}")
            }
        }
        handler.sendMessage(chatId, text.toString())
    }

    private fun Int.isEven() = this % 2 == 0
    private fun Int.isOdd() = this % 2 != 0

    companion object {
        val fieldString = """
            0💚 
            1❤️ 2🖤 3❤️ 4🖤 5❤️ 6🖤
            7❤️ 8🖤 9❤️ 10🖤 11❤️ ️12🖤
        """.trimIndent()
    }

}