package com.senderman.miniroulette

interface DBService {
    fun getCoins(userId: Int): Int
    fun addCoins(userId: Int, amount: Int)
    fun takeCoins(userId: Int, amount: Int)
    fun getLastRequestDate(userId: Int): Int
    fun setLastRequestDate(userId: Int, date: Int)
    fun getTop10(): LinkedHashMap<Int, Int>

    fun getWaitingBills(): Set<WaitingBill>
    fun addWaitingBill(bill: WaitingBill)
    fun removeBill(billId: String)

    companion object {
        const val startCoins: Int = 5000
    }
}