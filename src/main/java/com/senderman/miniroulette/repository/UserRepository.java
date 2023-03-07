package com.senderman.miniroulette.repository;

import com.senderman.miniroulette.model.User;
import io.micronaut.data.mongodb.annotation.MongoFindOptions;
import io.micronaut.data.mongodb.annotation.MongoFindQuery;
import io.micronaut.data.mongodb.annotation.MongoRepository;
import io.micronaut.data.mongodb.annotation.MongoUpdateOptions;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@MongoRepository
public interface UserRepository extends CrudRepository<User, Long> {

    @Override
    @MongoUpdateOptions(upsert = true)
    <S extends User> S update(@Valid @NotNull S entity);

    @Override
    @MongoUpdateOptions(upsert = true)
    <S extends User> Iterable<S> updateAll(@Valid @NotNull Iterable<S> entities);

    List<User> findByPendingCoinsNotEquals(int pendingCoins);

    @MongoFindQuery(filter = "{}", sort = "{ coins: -1 }")
    @MongoFindOptions(limit = 10)
    List<User> findTop10OrderByCoinsDesc();
}
