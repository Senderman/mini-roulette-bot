package com.senderman.miniroulette.gameobjects

sealed class Bet(val amount: Int, val stringTarget: String) {

    abstract val coefficient: Int

    abstract fun isWin(cell: Int): Boolean

    val pay: Int
        get() = amount * coefficient

    class Straight(amount: Int, private val target: Int) : Bet(amount, target.toString()) {
        override val coefficient = 11
        override fun isWin(cell: Int) = target == cell
    }

    class Color(amount: Int, private val color: COLOR) : Bet(amount, color.color) {
        override val coefficient = 1
        override fun isWin(cell: Int): Boolean {
            if (cell == 0) return false

            return when (cell % 2 == 0) {
                true -> color == COLOR.BLACK
                false -> color == COLOR.RED
            }
        }

        enum class COLOR(val color: String) {
            BLACK("черное"),
            RED("красное")
        }
    }

    sealed class RangeBet(amount: Int, private val first: Int, private val last: Int) : Bet(amount, "$first-$last") {

        override fun isWin(cell: Int) = cell in first..last

        class Split(amount: Int, first: Int, second: Int) : RangeBet(amount, first, second) {
            override val coefficient = 5
        }

        class Trio(amount: Int, first: Int, last: Int) : RangeBet(amount, first, last) {
            override val coefficient = 3
        }

        class Corner(amount: Int, first: Int, last: Int) : RangeBet(amount, first, last) {
            override val coefficient = 2
        }
    }

    companion object {

        enum class Type {
            STRAIGHT,
            SPLIT,
            TRIO,
            CORNER,
            COLORBET
        }

        /**
         * @param amount - amount of money player's ready to pay
         * @param target - input string with target, like ч, к, 3, 2-5
         * @return Bet subclass
         * @throws InvalidBetCommandException if input string format is invalid
         * @throws InvalidBetRangeException if range for bet is invalid (e.g. -1-228)
         */
        fun createBet(amount: Int, target: String): Bet = when (resolveBetType(target)) {
            Type.STRAIGHT -> Straight(amount, target.toInt())
            Type.COLORBET -> {
                val color = if (target[0] == 'ч') Color.COLOR.BLACK else Color.COLOR.RED
                Color(amount, color)
            }
            Type.SPLIT -> {
                val params = target.split("-")
                RangeBet.Split(amount, params[0].toInt(), params[1].toInt())
            }
            Type.TRIO -> {
                val params = target.split("-")
                RangeBet.Trio(amount, params[0].toInt(), params[1].toInt())
            }
            Type.CORNER -> {
                val params = target.split("-")
                RangeBet.Corner(amount, params[0].toInt(), params[1].toInt())
            }
        }

        private fun resolveBetType(target: String): Type = when {
            target.matches("ч(?:[её]рное)?|к(расное)?".toRegex()) -> Type.COLORBET
            target.matches("\\d+".toRegex()) -> {
                val cell = target.toInt()
                if (cell < 0 || cell > 12)
                    throw InvalidBetRangeException()
                Type.STRAIGHT
            }
            target.matches("\\d+-\\d+".toRegex()) -> {
                val params = target.split("-")
                val first = params[0].toInt()
                val second = params[1].toInt()
                if (first < 0 || second > 12 || first >= second)
                    throw InvalidBetRangeException()
                when (second - first) {
                    1 -> Type.SPLIT
                    2 -> Type.TRIO
                    3 -> Type.CORNER
                    else -> throw InvalidBetRangeException()
                }
            }
            else -> throw InvalidBetCommandException()
        }
    }
}

class InvalidBetCommandException : Exception()
class InvalidBetRangeException : Exception()