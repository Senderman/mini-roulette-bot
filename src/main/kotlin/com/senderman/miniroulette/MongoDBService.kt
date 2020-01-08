package com.senderman.miniroulette

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.eq
import com.senderman.neblib.MongoClientKeeper
import org.bson.Document

class MongoDBService : DBService {
    operator fun MongoDatabase.get(s: String): MongoCollection<Document> = getCollection(s)

    private val database: MongoDatabase = MongoClientKeeper.client.getDatabase("roulette")
    private val users: MongoCollection<Document> = database["users"]
    private val bills: MongoCollection<Document> = database["bills"]

    private fun getUser(userId: Int): Document {
        val doc = users.find(eq("userId", userId)).first()
        if (doc == null) {
            val commit = Document("userId", userId).append("coins", DBService.startCoins).append("lastReqDate", 0)
            users.insertOne(commit)
            return commit
        }
        return doc
    }

    override fun getCoins(userId: Int): Int = getUser(userId).getInteger("coins")

    override fun addCoins(userId: Int, amount: Int) {
        getUser(userId)
        users.updateOne(
            eq("userId", userId),
            Document("\$inc", Document("coins", amount))
        )
    }

    override fun takeCoins(userId: Int, amount: Int) {
        getUser(userId)
        users.updateOne(
            eq("userId", userId),
            Document("\$inc", Document("coins", -amount))
        )
    }

    override fun getLastRequestDate(userId: Int): Int = getUser(userId).getInteger("lastReqDate")

    override fun setLastRequestDate(userId: Int, date: Int) {
        getUser(userId)
        users.updateOne(
            eq("userId", userId),
            Document("\$set", Document("lastReqDate", date))
        )
    }

    override fun getTop10(): LinkedHashMap<Int, Int> {
        val result = LinkedHashMap<Int, Int>()
        val players = users.find().sort(Document("coins", -1)).limit(10)
        players.forEach { result[it.getInteger("userId")] = it.getInteger("coins") }
        return result
    }

    override fun getWaitingBills(): Set<WaitingBill> {
        val result = HashSet<WaitingBill>()
        bills.find().forEach {
            result.add(
                WaitingBill(
                    it.getInteger("userId"),
                    it.getInteger("coins"),
                    it.getString("billId")
                )
            )
        }
        return result
    }

    override fun addWaitingBill(bill: WaitingBill) {
        bills.insertOne(Document("billId", bill.billId).append("userId", bill.userId).append("coins", bill.coins))
    }

    override fun removeBill(billId: String) {
        bills.deleteOne(eq("billId", billId))
    }
}