package com.senderman.miniroulette

interface DBService {
    fun getCoins(userId: Int): Int
    fun addCoins(userId: Int, amount: Int)
    fun takeCoins(userId: Int, amount: Int)
    fun getLastRequestDate(userId: Int): Long
    fun setLastRequestDate(userId: Int, date: Long)
}