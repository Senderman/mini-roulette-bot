package com.senderman.miniroulette.game;

import jakarta.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class GameManagerImpl<ID, T extends Game<ID>> implements GameManager<ID, T> {

    private final Map<ID, T> games;

    public GameManagerImpl() {
        this.games = new ConcurrentHashMap<>();
    }

    @Override
    synchronized public T get(long id) {
        return games.get(id);
    }

    @Override
    synchronized public void save(T game) {
        if (!games.containsKey(game.getId()))
            games.put(game.getId(), game);
    }

    @Override
    synchronized public boolean exists(ID id) {
        return games.containsKey(id);
    }

    @Override
    synchronized public void delete(ID id) {
        games.remove(id);
    }
}
