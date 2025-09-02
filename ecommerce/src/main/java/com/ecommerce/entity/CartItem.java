package com.ecommerce.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "cart_items")
public class CartItem {

    @Id
    private String id; // MongoDB ObjectId (String)

    @DBRef(lazy = true)   // reference to another document
    private User user;

    @DBRef(lazy = true)   // reference to another document
    private Product product;

    private Integer quantity;
    private BigDecimal unitPrice;

    // Business logic: ensure defaults
    public void ensureUnitPrice() {
        if (unitPrice == null && product != null && product.getPrice() != null) {
            unitPrice = product.getPrice();
        }
        if (unitPrice == null) throw new IllegalStateException("Unit price cannot be null in CartItem.");
        if (quantity == null || quantity < 1) quantity = 1;
    }

    // ---- Getters/Setters ----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
