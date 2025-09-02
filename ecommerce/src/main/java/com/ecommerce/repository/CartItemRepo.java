package com.ecommerce.repository;

import com.ecommerce.entity.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepo extends MongoRepository<CartItem, String> {

    // Find cart items for a user
    List<CartItem> findByUser_Id(String userId);

    // Find specific cart item for a user and product
    Optional<CartItem> findByUser_IdAndProduct_Id(String userId, String productId);

    // Delete all cart items for a user
    void deleteByUser_Id(String userId);
}
