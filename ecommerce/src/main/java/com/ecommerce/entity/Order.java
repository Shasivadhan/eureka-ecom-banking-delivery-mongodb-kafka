package com.ecommerce.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "orders")
public class Order {

    @Id
    private String id; // MongoDB ObjectId

    @DBRef(lazy = true)
    private User user; // Reference to User document

    private BigDecimal totalAmount = BigDecimal.ZERO;
    private String status = "PLACED";
    private LocalDateTime purchaseDate = LocalDateTime.now();

    @DBRef(lazy = true) // store references to OrderItem documents
    private List<OrderItem> items = new ArrayList<>();

    /* ===== Convenience helpers ===== */
    public void addItem(OrderItem item) {
        if (item == null) return;
        items.add(item);
        // ⚡ In Mongo, no need to "setOrder(this)" because references are looser.
    }

    public void removeItem(OrderItem item) {
        if (item == null) return;
        items.remove(item);
    }

    /* ===== Getters/Setters ===== */
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
