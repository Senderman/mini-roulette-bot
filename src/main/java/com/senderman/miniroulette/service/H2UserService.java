package com.senderman.miniroulette.service;

import com.senderman.miniroulette.model.User;
import com.senderman.miniroulette.repository.UserRepository;
import jakarta.inject.Singleton;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Singleton
public class H2UserService implements UserService {

    private final UserRepository repo;

    public H2UserService(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public User findById(long id, String name) {
        return repo.findById(id).orElseGet(() ->
                repo.save(new User(id, name, INITIAL_COINS_QUANTITY, 0, Timestamp.valueOf(LocalDateTime.of(1970, 1, 1, 0, 0))))
        );
    }

    @Override
    public User save(User user) {
        if (repo.existsById(user.getUserId()))
            return repo.update(user);
        else
            return repo.save(user);
    }

    @Override
    public List<User> saveAll(Collection<User> users) {
        return users.stream().map(this::save).toList();
    }

    @Override
    public void updateCoins(long userId, String name, int coins, int pendingCoins) {
        repo.updateCoins(userId, name, coins, pendingCoins);
    }

    @Override
    public void increaseCoinsSetLastCoinRequestDate(long userId, int coins, Timestamp lastCoinRequestDate) {
        repo.increaseCoinsSetLastCoinRequestDate(userId, coins, lastCoinRequestDate);
    }

    @Override
    public List<User> findByPendingCoinsNotEquals(int pendingCoins) {
        return repo.findByPendingCoinsNotEquals(pendingCoins);
    }

    @Override
    public List<User> findTop10OrderByCoinsDesc() {
        return repo.findTop10OrderByCoinsDesc();
    }
}
