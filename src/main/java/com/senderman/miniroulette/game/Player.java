package com.senderman.miniroulette.game;

import com.senderman.miniroulette.game.bet.Bet;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final long id;
    private final String name;
    private final List<Bet> bets;
    private int delta;

    public Player(long id, String name) {
        this.id = id;
        this.name = name;
        this.bets = new ArrayList<>();
        this.delta = 0;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Bet> getBets() {
        return bets;
    }

    public void addBet(Bet bet) {
        bets.add(bet);
    }

    public int getDelta() {
        return delta;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }
}
