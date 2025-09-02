package com.ecommerce.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DeliveryEvent {
    private String orderId;
    private String userId;
    private List<String> productNames;
    private BigDecimal totalAmount;
    private String deliveryDate; // example: 2025-09-05
}
