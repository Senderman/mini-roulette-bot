package com.senderman.miniroulette

import java.sql.Connection
import java.sql.DriverManager
import java.util.*

class SqlDBService : DBService {

    private val connection: Connection
    private val billTable = "bills"
    private val usersTable = "users"

    init {
        val connectionProps = Properties()
        connectionProps["user"] = ""
        connectionProps["password"] = ""

        connection = DriverManager.getConnection("")

        val st = connection.createStatement()
        st.execute(
            """
               CREATE TABLE IF NOT EXISTS $usersTable(
               user_id INT NOT NULL UNIQUE,
               coins INT NOT NULL,
               last300date INT,
               last10date INT
               )
           """.trimIndent()
        )

        val st2 = connection.createStatement()
        st2.execute(
            """
               CREATE TABLE IF NOT EXISTS $billTable(
               user_id INT NOT NULL,
               coins INT NOT NULL,
               bill_id VARCHAR(36) NOT NULL UNIQUE
               )
            """.trimIndent()
        )
    }

    override fun getCoins(userId: Int): Int {
        val resultSet = connection
            .createStatement()
            .executeQuery("SELECT coins FROM $usersTable WHERE user_id = $userId")
        resultSet.next()
        return resultSet.getInt("coins")
    }

    override fun addCoins(userId: Int, amount: Int) {
        connection
            .createStatement()
            .execute(
                "UPDATE $usersTable" +
                        "SET coins = coins + $amount" +
                        "WHERE user_id = $userId"
            )
    }

    override fun takeCoins(userId: Int, amount: Int) {
        connection
            .createStatement()
            .execute(
                "UPDATE $usersTable" +
                        "SET coins = coins - $amount" +
                        "WHERE user_id = $userId"
            )
    }

    override fun setLast300RequestDate(userId: Int, date: Int) {
        TODO("not implemented")
    }

    override fun getLast300RequestDate(userId: Int): Int {
        TODO("not implemented")
    }

    override fun setLast10RequestDate(userId: Int, date: Int) {
        TODO("not implemented")
    }

    override fun getLast10RequestDate(userId: Int): Int {
        TODO("not implemented")
    }

    override fun getTop10(): LinkedHashMap<Int, Int> {
        TODO("not implemented")
    }

    override fun getWaitingBills(): Set<WaitingBill> {
        TODO("not implemented")
    }

    override fun addWaitingBill(bill: WaitingBill) {
        val st = connection.createStatement()
        st.execute(
            "INSERT INTO $billTable VALUES(${bill.userId}, ${bill.coins}, ${bill.billId})"
        
        )
    }

    override fun removeBill(billId: String) {
        val st = connection.createStatement()
        st.execute(
            "DELETE from $billTable WHERE bill_id = $billId"
        )
    }


}