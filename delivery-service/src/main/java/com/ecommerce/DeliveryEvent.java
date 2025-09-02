package com.ecommerce.kafka;

import java.math.BigDecimal;
import java.util.List;

public class DeliveryEvent {

    private String orderId;
    private String userId;
    private List<String> productNames;
    private BigDecimal totalAmount;
    private String deliveryDate;

    // --- Getters & Setters ---
    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getProductNames() {
        return productNames;
    }
    public void setProductNames(List<String> productNames) {
        this.productNames = productNames;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }
    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    @Override
    public String toString() {
        return "DeliveryEvent{" +
                "orderId='" + orderId + '\'' +
                ", userId='" + userId + '\'' +
                ", productNames=" + productNames +
                ", totalAmount=" + totalAmount +
                ", deliveryDate='" + deliveryDate + '\'' +
                '}';
    }
}
