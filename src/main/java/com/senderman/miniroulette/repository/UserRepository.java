package com.senderman.miniroulette.repository;

import com.senderman.miniroulette.model.User;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface UserRepository extends CrudRepository<User, Long> {

    @Query("""
            UPDATE "user" SET
            name = :name,
            coins = coins + (:coins),
            pending_coins = pending_coins + (:pendingCoins)
            WHERE user_id = :userId
            """)
    void updateCoins(long userId, String name, int coins, int pendingCoins);

    @Query("UPDATE \"user\" SET coins = coins + :coins, last_coin_request_date = :lastCoinRequestDate WHERE user_id = :userId")
    void increaseCoinsSetLastCoinRequestDate(long userId, int coins, Timestamp lastCoinRequestDate);

    List<User> findByPendingCoinsNotEquals(int pendingCoins);

    List<User> findTop10OrderByCoinsDesc();
}
