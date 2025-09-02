package com.delivery.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DeliveryRequest {
    private String orderId;
    private String userId;
    private List<String> products;
    private BigDecimal invoiceTotal;
    private LocalDate deliveryDate;
    private String status; // PENDING, SHIPPED, DELIVERED

    // getters & setters
}
