package com.delivery.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "deliveries")
public class Delivery {

    @Id
    private String id;
    private String orderId;
    private String userId;
    private List<String> productNames;
    private Double totalAmount;
    private String deliveryDate;   // ✅ String to match DeliveryEvent
    private String status;

    // --- Getters & Setters ---
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

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

    public Double getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }
    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "id='" + id + '\'' +
                ", orderId='" + orderId + '\'' +
                ", userId='" + userId + '\'' +
                ", productNames=" + productNames +
                ", totalAmount=" + totalAmount +
                ", deliveryDate='" + deliveryDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
