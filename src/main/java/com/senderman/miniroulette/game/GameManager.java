package com.senderman.miniroulette.game;

import io.micronaut.core.annotation.Nullable;

public interface GameManager<ID> {

    /**
     * Get game by id
     *
     * @param id id of the game
     * @return game, or null if doesn't exist
     */
    @Nullable
    Game<ID> get(long id);

    /**
     * Save a new game. If game with the given id exists, do nothing
     *
     * @param game game to save
     */
    void save(Game<ID> game);

    /**
     * Check if game exists by id
     *
     * @param id id of the game
     * @return true if exists, otherwise false
     */
    boolean exists(ID id);

    /**
     * Delete game by id. If game with given id doesn't exist, do nothing
     *
     * @param id id of the game to delete
     */
    void delete(ID id);

}
