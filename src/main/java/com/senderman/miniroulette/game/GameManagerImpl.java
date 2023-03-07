package com.senderman.miniroulette.game;

import jakarta.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class GameManagerImpl<ID> implements GameManager<ID> {

    private final Map<ID, Game<ID>> games;

    public GameManagerImpl() {
        this.games = new ConcurrentHashMap<>();
    }

    @Override
    synchronized public Game<ID> get(long id) {
        return games.get(id);
    }

    @Override
    synchronized public void save(Game<ID> game) {
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
