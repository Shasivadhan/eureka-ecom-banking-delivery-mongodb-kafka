package com.ecommerce.dto;

import lombok.Data;

@Data
public class DebitRequest {
    private String userId;      // ✅ Matches Mongo _id
    private String accountNumber;
    private double amount;
    private String description;
}