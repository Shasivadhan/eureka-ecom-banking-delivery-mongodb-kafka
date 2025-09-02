package com.delivery.repository;
import java.util.Optional;
import com.delivery.entity.Delivery;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeliveryRepository extends MongoRepository<Delivery, String> {
    Optional<Delivery> findByOrderId(String orderId);
}
