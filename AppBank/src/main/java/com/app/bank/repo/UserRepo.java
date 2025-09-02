package com.app.bank.repo;

import com.app.bank.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepo extends MongoRepository<User, Long> {
    Optional<User> findByAccount_AccountNumber(String accountNumber);
}
