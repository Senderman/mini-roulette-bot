package com.senderman.miniroulette.model;

import io.micronaut.core.annotation.Creator;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;

import java.sql.Timestamp;
import java.util.Objects;

@MappedEntity("user")
public class User {

    @Id
    @MappedProperty("user_id")
    private final long userId;

    @MappedProperty("name")
    private String name;

    @MappedProperty("coins")
    private int coins;

    @MappedProperty("pending_coins")
    private int pendingCoins; // coins invested in current games

    @MappedProperty("last_coin_request_date")
    private Timestamp lastCoinRequestDate;

    @Creator
    public User(@Id long userId, String name, int coins, int pendingCoins, Timestamp lastCoinRequestDate) {
        this.userId = userId;
        this.name = name;
        this.coins = coins;
        this.pendingCoins = pendingCoins;
        this.lastCoinRequestDate = lastCoinRequestDate;
    }

    public long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getPendingCoins() {
        return pendingCoins;
    }

    public void setPendingCoins(int pendingCoins) {
        this.pendingCoins = pendingCoins;
    }

    public Timestamp getLastCoinRequestDate() {
        return lastCoinRequestDate;
    }

    public void setLastCoinRequestDate(Timestamp lastCoinRequestDate) {
        this.lastCoinRequestDate = lastCoinRequestDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
