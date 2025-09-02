package com.ecommerce.controller;

import com.ecommerce.dto.PurchaseRequest;
import com.ecommerce.dto.PurchaseResponse;
import com.ecommerce.service.OrderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/purchase")
    public ResponseEntity<PurchaseResponse> purchase(@RequestBody PurchaseRequest request) {
        log.info("Received purchase request: userId={} account={}",
                request.getUserId(), request.getAccountNumber());

        PurchaseResponse response = orderService.purchaseItems(
                request.getUserId(),       // ✅ now String
                request.getAccountNumber(),
                request.getDescription()
        );

        log.debug("purchase() returned: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<?> getMonthlyDashboard(@PathVariable String userId) {  // ✅ Long → String
        log.info("Fetching dashboard for userId={}", userId);
        var dashboard = orderService.getMonthlyDashboard(userId);
        log.debug("dashboard data for userId={}: {}", userId, dashboard);
        return ResponseEntity.ok(dashboard);
    }
}
