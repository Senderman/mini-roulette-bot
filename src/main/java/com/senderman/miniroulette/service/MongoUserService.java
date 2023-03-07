package com.senderman.miniroulette.service;

import com.senderman.miniroulette.model.User;
import com.senderman.miniroulette.repository.UserRepository;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class MongoUserService implements UserService {

    private final UserRepository repo;

    public MongoUserService(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public User findById(long id) {
        return repo.findById(id).orElseGet(() -> new User(id, "Без имени", INITIAL_COINS_QUANTIY, 0, 0));
    }

    @Override
    public User save(User user) {
        return repo.update(user);
    }

    @Override
    public Iterable<User> saveAll(Iterable<User> users) {
        return repo.updateAll(users);
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
