package com.senderman.miniroulette

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.eq
import com.senderman.neblib.MongoClientKeeper
import org.bson.Document

class MongoDBService : DBService {
    private val database: MongoDatabase = MongoClientKeeper.client.getDatabase("roulette")
    private val users: MongoCollection<Document> = database.getCollection("users")

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
}