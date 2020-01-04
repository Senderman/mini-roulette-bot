package com.senderman.miniroulette

import com.mongodb.client.model.Filters.eq
import com.senderman.neblib.MongoClientKeeper
import org.bson.Document

class MongoDBService : DBService {
    val database = MongoClientKeeper.client.getDatabase("roulette")
    val users = database.getCollection("users")

    private fun getUser(userId: Int): Document {
        val doc = users.find(eq("userId", userId)).first()
        if (doc == null) {
            val commit = Document("userId", userId).append("coins", 5000).append("lastReqDate", 0.toLong())
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
            Document("\$dec", Document("coins", amount))
        )
    }

    override fun getLastRequestDate(userId: Int): Long = getUser(userId).getLong("lastReqDate")

    override fun setLastRequestDate(userId: Int, date: Long) {
        getUser(userId)
        users.updateOne(
            eq("userId", userId),
            Document("\$set", Document("lastReqDate", date))
        )
    }
}