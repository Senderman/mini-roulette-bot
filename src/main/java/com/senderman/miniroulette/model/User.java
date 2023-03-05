package com.senderman.miniroulette.model;

import io.micronaut.core.annotation.Creator;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.util.Objects;

@MappedEntity("user")
public class User {

    @Id
    private final long userId;
    private int coins;
    private int lastCoinRequestDate;
    private int last10CoinRequestDate;

    @Creator
    public User(@Id long userId, int coins, int lastCoinRequestDate, int last10CoinRequestDate) {
        this.userId = userId;
        this.coins = coins;
        this.lastCoinRequestDate = lastCoinRequestDate;
        this.last10CoinRequestDate = last10CoinRequestDate;
    }

    public long getUserId() {
        return userId;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getLastCoinRequestDate() {
        return lastCoinRequestDate;
    }

    public void setLastCoinRequestDate(int lastCoinRequestDate) {
        this.lastCoinRequestDate = lastCoinRequestDate;
    }

    public int getLast10CoinRequestDate() {
        return last10CoinRequestDate;
    }

    public void setLast10CoinRequestDate(int last10CoinRequestDate) {
        this.last10CoinRequestDate = last10CoinRequestDate;
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
