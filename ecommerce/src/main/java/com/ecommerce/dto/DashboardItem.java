package com.ecommerce.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DashboardItem {
    private String name;
    private BigDecimal price;
    private int quantity;
}
