package com.senderman.miniroulette.repository;

import com.senderman.miniroulette.model.User;
import io.micronaut.data.mongodb.annotation.MongoRepository;
import io.micronaut.data.repository.CrudRepository;

@MongoRepository
public interface UserRepository extends CrudRepository<User, Long> {

}
