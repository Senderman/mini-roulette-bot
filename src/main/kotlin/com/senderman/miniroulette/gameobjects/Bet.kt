package com.senderman.miniroulette.gameobjects

import com.senderman.miniroulette.gameobjects.BetType.*
import com.senderman.neblib.TgUser

enum class COLOR {
    BLACK,
    RED
}

sealed class Bet(val player: TgUser, val amount: Int) {
    abstract val coefficient: Int

    val pay: Int
        get() = amount * coefficient

    class Straight(player: TgUser, amount: Int, val target: Int) : Bet(player, amount) {
        override val coefficient = 11
    }

    class Split(player: TgUser, amount: Int, val first: Int, val second: Int) : Bet(player, amount) {
        override val coefficient = 5
    }

    class Trio(player: TgUser, amount: Int, val first: Int, val last: Int) : Bet(player, amount) {
        override val coefficient = 3
    }

    class Corner(player: TgUser, amount: Int, val first: Int, val last: Int) : Bet(player, amount) {
        override val coefficient = 2
    }

    class Color(player: TgUser, amount: Int, val color: COLOR) : Bet(player, amount) {
        override val coefficient = 1
    }

    companion object {
        /**
         * @param target - input string with target, like ч,к, 3, 2-5
         */
        fun createBet(player: TgUser, amount: Int, type: BetType, target: String): Bet = when (type) {
            STRAIGHT -> Straight(player, amount, target[0].toInt())
            SPLIT -> Split(player, amount, target[0].toInt(), target[1].toInt())
            TRIO -> Trio(player, amount, target[0].toInt(), target[1].toInt())
            CORNER -> Corner(player, amount, target[0].toInt(), target[1].toInt())
            COLORBET -> {
                val color = if (target[0] == 'ч') COLOR.BLACK else COLOR.RED
                Color(player, amount, color)
            }
        }

        /**
         * @param target - input string with target, like ч,к, 3, 2-5
         * @return BetType for input
         * @throws InvalidBetCommandException if input string format is invalid
         * @throws InvalidBetRangeException if range for bet is invalid (e.g. -1-228)
         */
        fun resolveBetType(target: String): BetType = when {
            target.matches("\\d+".toRegex()) -> STRAIGHT
            target.matches("ч(:?[её]рное)?|к(расное)?".toRegex()) -> COLORBET
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