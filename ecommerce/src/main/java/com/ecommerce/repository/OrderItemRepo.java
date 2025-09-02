package com.ecommerce.repository;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderItemRepo extends MongoRepository<OrderItem, String> {
    List<OrderItem> findByOrder(Order order);
}
