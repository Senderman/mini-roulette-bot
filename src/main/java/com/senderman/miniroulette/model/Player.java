package com.senderman.miniroulette.model;

public class Player {

    private final long id;
    private final String name;

    public Player(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
