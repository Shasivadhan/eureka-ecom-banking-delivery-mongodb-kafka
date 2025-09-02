package com.ecommerce.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardResponse {
    private String month;
    private BigDecimal totalPurchaseAmount;
    private List<DashboardItem> items;
}
