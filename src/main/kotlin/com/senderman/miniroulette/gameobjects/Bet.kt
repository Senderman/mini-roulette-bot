package com.senderman.miniroulette.gameobjects

import com.senderman.miniroulette.gameobjects.BetType.*

enum class COLOR(val color:String) {
    BLACK("черное"),
    RED("красное")
}

sealed class Bet(val amount: Int, val stringTarget: String) {
    abstract val coefficient: Int

    val pay: Int
        get() = amount * coefficient

    class Straight(amount: Int, val target: Int) : Bet(amount, target.toString()) {
        override val coefficient = 11
    }

    class Split(amount: Int, val first: Int, val second: Int) :
        Bet(amount, "$first-$second") {
        override val coefficient = 5
    }

    class Trio(amount: Int, val first: Int, val last: Int) :
        Bet(amount, "$first-$last") {
        override val coefficient = 3
    }

    class Corner(amount: Int, val first: Int, val last: Int) :
        Bet(amount, "$first-$last") {
        override val coefficient = 2
    }

    class Color(amount: Int, val color: COLOR) :
        Bet(amount, color.color) {
        override val coefficient = 1
    }

    companion object {
        /**
         * @param amount - amount of money player's ready to pay
         * @param target - input string with target, like ч,к, 3, 2-5
         * @return Bet subclass
         * @throws InvalidBetCommandException if input string format is invalid
         * @throws InvalidBetRangeException if range for bet is invalid (e.g. -1-228)
         */
        fun createBet(amount: Int, target: String): Bet = when (resolveBetType(target)) {
            STRAIGHT -> Straight(amount, target.toInt())
            COLORBET -> {
                val color = if (target[0] == 'ч') COLOR.BLACK else COLOR.RED
                Color(amount, color)
            }
            SPLIT -> {
                val params = target.split("-")
                Split(amount, params[0].toInt(), params[1].toInt())
            }
            TRIO -> {
                val params = target.split("-")
                Trio(amount, params[0].toInt(), params[1].toInt())
            }
            CORNER -> {
                val params = target.split("-")
                Corner(amount, params[0].toInt(), params[1].toInt())
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