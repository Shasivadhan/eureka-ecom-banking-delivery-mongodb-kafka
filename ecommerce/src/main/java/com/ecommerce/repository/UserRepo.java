package com.ecommerce.repository;

import com.ecommerce.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepo extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // ⚠️ Note: In production, you should not query by password directly (security risk).
    Optional<User> findByEmailAndPassword(String email, String password);
}
