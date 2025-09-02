package com.ecommerce.dto;

import lombok.Data;

@Data
public class CartItemRequest {
    private String userId;     // ✅ changed from Long
    private String productId;  // ✅ changed from Long
    private int quantity;

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
