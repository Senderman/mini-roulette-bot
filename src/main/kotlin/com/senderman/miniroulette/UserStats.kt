package com.senderman.miniroulette

import com.senderman.neblib.TgUser

class UserStats(userId: Int, name: String, val coins: Int) : TgUser(userId, name), Comparable<UserStats> {
    override fun compareTo(other: UserStats): Int = this.coins.compareTo(other.coins)
}