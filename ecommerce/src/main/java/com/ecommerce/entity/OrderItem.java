package com.ecommerce.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.annotation.Transient; // ✅ use Spring Data's general Transient

import java.math.BigDecimal;

@Document(collection = "order_items")
public class OrderItem {

    @Id
    private String id; // MongoDB ObjectId

    @DBRef(lazy = true)
    private Order order; // reference to Order document

    @DBRef(lazy = true)
    private Product product; // reference to Product document

    private Integer quantity;
    private BigDecimal priceAtPurchase;

    // -------------------
    // Getters / Setters
    // -------------------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPriceAtPurchase() { return priceAtPurchase; }
    public void setPriceAtPurchase(BigDecimal priceAtPurchase) { this.priceAtPurchase = priceAtPurchase; }

    // -------------------
    // Convenience aliases
    // -------------------

    /** Alias for compatibility with service code. */
    public BigDecimal getPrice() { return priceAtPurchase; }

    /** Alias for compatibility with service code. */
    public void setPrice(BigDecimal price) { this.priceAtPurchase = price; }

    /** Useful helper: price * quantity (not stored in DB). */
    @Transient
    public BigDecimal getLineTotal() {
        if (priceAtPurchase == null || quantity == null) return BigDecimal.ZERO;
        return priceAtPurchase.multiply(BigDecimal.valueOf(quantity));
    }
}
