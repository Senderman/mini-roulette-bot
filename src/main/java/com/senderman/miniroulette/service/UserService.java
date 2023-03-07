package com.senderman.miniroulette.service;

import com.senderman.miniroulette.model.User;

import java.util.List;

public interface UserService {

    int INITIAL_COINS_QUANTIY = 1000;

    User findById(long id);

    User save(User user);

    Iterable<User> saveAll(Iterable<User> users);

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
