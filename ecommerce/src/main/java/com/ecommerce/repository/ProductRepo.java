package com.ecommerce.repository;

import com.ecommerce.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ProductRepo extends MongoRepository<Product, String> {
    List<Product> findByNameContainingIgnoreCase(String keyword);
}
