package com.delivery.controller;

import com.delivery.entity.Delivery;
import com.delivery.repository.DeliveryRepository;  // ✅ updated package
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryRepository deliveryRepository;  // ✅ updated type

    public DeliveryController(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @GetMapping
    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Delivery> getByOrderId(@PathVariable String orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
