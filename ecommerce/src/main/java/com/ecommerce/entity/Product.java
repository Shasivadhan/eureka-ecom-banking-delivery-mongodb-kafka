package com.ecommerce.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "products")
public class Product {

    @Id
    private String id;  // MongoDB ObjectId stored as String

    private String name;
    private String category;
    private BigDecimal price = BigDecimal.ZERO; // default to avoid nulls
    private Integer stock = 0;                  // default to avoid nulls

    public Product() {}

    // Manual defaults instead of @PrePersist
    public void applyDefaults() {
        if (price == null) price = BigDecimal.ZERO;
        if (stock == null) stock = 0;
    }

    // Getters/Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
