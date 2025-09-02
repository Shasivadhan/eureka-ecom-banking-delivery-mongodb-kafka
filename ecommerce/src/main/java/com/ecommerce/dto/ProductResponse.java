package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private String id;       // ✅ changed from Long → String
    private String name;
    private String category;
    private BigDecimal price;
    private int stock;

    // ✅ Optional constructor for quick test data creation
    public ProductResponse(String id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}
