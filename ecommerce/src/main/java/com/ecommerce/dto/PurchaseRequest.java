package com.ecommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class PurchaseRequest {
    @Schema(example = "64b1a2f9c9d2b52d9f1a3e45") // ✅ example Mongo ObjectId
    private String userId;

    @Schema(example = "123456789012")
    private String accountNumber;

    @Schema(example = "Purchase from E-Commerce", required = false)
    private String description;

    // getters/setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
