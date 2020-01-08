package com.senderman.miniroulette.gameobjects

import com.senderman.neblib.TgUser

class Player(id: Int, name: String, var coins:Int = 0): TgUser(id, name), Comparable<Player>{
    val bets = ArrayList<Bet>()
    override fun compareTo(other: Player): Int = this.coins.compareTo(other.coins)
}