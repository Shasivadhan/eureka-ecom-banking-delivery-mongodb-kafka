package com.delivery.kafka;

import com.delivery.entity.Delivery;
import com.delivery.repository.DeliveryRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DeliveryConsumer {

    private final DeliveryRepository deliveryRepository;

    public DeliveryConsumer(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    // ✅ Consume DeliveryEvent directly (no manual JSON parsing)
    @KafkaListener(topics = "delivery-requests", groupId = "delivery-service-group")
    public void consume(com.ecommerce.kafka.DeliveryEvent event) {
        System.out.println("📦 Received delivery request: " + event);

        Delivery delivery = new Delivery();
        delivery.setOrderId(event.getOrderId());
        delivery.setUserId(event.getUserId());
        delivery.setProductNames(event.getProductNames());
        delivery.setTotalAmount(event.getTotalAmount().doubleValue());
        delivery.setDeliveryDate(event.getDeliveryDate());
        delivery.setStatus("PENDING");

        deliveryRepository.save(delivery);
        System.out.println("✅ Delivery saved in MongoDB for Order ID: " + event.getOrderId());
    }
}
