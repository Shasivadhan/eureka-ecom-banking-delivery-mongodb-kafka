package com.ecommerce.service;

import com.ecommerce.dto.PurchaseResponse;
import com.ecommerce.dto.DashboardResponse;
import java.util.List;

public interface OrderService {

    /**
     * Computes total from the user's cart (unitPrice * quantity),
     * calls the bank to debit that amount, creates the Order & OrderItems,
     * clears the cart, and returns a summary.
     */
    PurchaseResponse purchaseItems(String userId, String accountNumber, String description);

    /**
     * Returns month-wise dashboard data for the given user.
     */
    List<DashboardResponse> getMonthlyDashboard(String userId);
}
