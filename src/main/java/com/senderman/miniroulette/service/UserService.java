package com.senderman.miniroulette.service;

import com.senderman.miniroulette.model.User;
import io.micronaut.data.annotation.Id;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

public interface UserService {

    int INITIAL_COINS_QUANTITY = 1000;

    User findById(long id);

    User save(User user);

    List<User> saveAll(Collection<User> users);

    void updateCoins(@Id long userId, int coins, int pendingCoins);

    void increaseCoinsSetLastCoinRequestDate(@Id long userId, int coins, Timestamp lastCoinRequestDate);

    List<User> findByPendingCoinsNotEquals(int pendingCoins);

    List<User> findTop10OrderByCoinsDesc();

    default void restoreLostCoins() {
        var users = findByPendingCoinsNotEquals(0);
        if (users.isEmpty())
            return;
        users.forEach(u -> {
            u.setCoins(u.getCoins() + u.getPendingCoins());
            u.setPendingCoins(0);
        });
        saveAll(users);
    }

}
