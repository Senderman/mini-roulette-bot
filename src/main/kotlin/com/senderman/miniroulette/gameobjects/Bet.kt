package com.senderman.miniroulette.gameobjects

import com.senderman.miniroulette.gameobjects.BetType.*
import com.senderman.neblib.TgUser

enum class COLOR {
    BLACK,
    RED
}

sealed class Bet(val player: TgUser, val amount: Int, val stringTarget: String) {
    abstract val coefficient: Int

    val pay: Int
        get() = amount * coefficient

    class Straight(player: TgUser, amount: Int, val target: Int) : Bet(player, amount, target.toString()) {
        override val coefficient = 11
    }

    class Split(player: TgUser, amount: Int, val first: Int, val second: Int) :
        Bet(player, amount, "$first-$second") {
        override val coefficient = 5
    }

    class Trio(player: TgUser, amount: Int, val first: Int, val last: Int) :
        Bet(player, amount, "$first-$last") {
        override val coefficient = 3
    }

    class Corner(player: TgUser, amount: Int, val first: Int, val last: Int) :
        Bet(player, amount, "$first-$last") {
        override val coefficient = 2
    }

    class Color(player: TgUser, amount: Int, val color: COLOR) :
        Bet(player, amount, if (color == COLOR.BLACK) "черное" else "красное") {
        override val coefficient = 1
    }

    companion object {
        /**
         * @param player - one who bets
         * @param amount - amount of money player's ready to pay
         * @param target - input string with target, like ч,к, 3, 2-5
         * @return Bet subclass
         * @throws InvalidBetCommandException if input string format is invalid
         * @throws InvalidBetRangeException if range for bet is invalid (e.g. -1-228)
         */
        fun createBet(player: TgUser, amount: Int, target: String): Bet = when (resolveBetType(target)) {
            STRAIGHT -> Straight(player, amount, target.toInt())
            COLORBET -> {
                val color = if (target == "ч") COLOR.BLACK else COLOR.RED
                Color(player, amount, color)
            }
            SPLIT -> {
                val params = target.split("-")
                Split(player, amount, params[0].toInt(), params[1].toInt())
            }
            TRIO -> {
                val params = target.split("-")
                Trio(player, amount, params[0].toInt(), params[1].toInt())
            }
            CORNER -> {
                val params = target.split("-")
                Corner(player, amount, params[0].toInt(), params[1].toInt())
            }
        }

        private fun resolveBetType(target: String): BetType = when {
            target.matches("ч(:?[её]рное)?|к(расное)?".toRegex()) -> COLORBET
            target.matches("\\d+".toRegex()) -> {
                val cell = target.toInt()
                if (cell < 0 || cell > 12)
                    throw InvalidBetRangeException()
                STRAIGHT
            }
            target.matches("\\d+-\\d+".toRegex()) -> {
                val params = target.split("-".toRegex())
                val first = params[0].toInt()
                val second = params[1].toInt()
                if (first < 0 || second > 12 || first >= second)
                    throw InvalidBetRangeException()
                when (second - first) {
                    1 -> SPLIT
                    2 -> TRIO
                    3 -> CORNER
                    else -> throw InvalidBetRangeException()
                }
            }
            else -> throw InvalidBetCommandException()
        }
    }
}

class InvalidBetCommandException : Exception()
class InvalidBetRangeException : Exception()