package com.senderman.miniroulette.model;

import io.micronaut.core.annotation.Creator;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.util.Objects;

@MappedEntity("user")
public class User {

    @Id
    private final long userId;
    private String name;
    private int coins;
    private int pendingCoins; // coins invested in current games
    private int lastCoinRequestDate;

    @Creator
    public User(@Id long userId, String name, int coins, int pendingCoins, int lastCoinRequestDate) {
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

    public int getLastCoinRequestDate() {
        return lastCoinRequestDate;
    }

    public void setLastCoinRequestDate(int lastCoinRequestDate) {
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
