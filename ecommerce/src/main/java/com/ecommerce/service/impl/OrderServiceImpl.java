package com.ecommerce.service.impl;

import com.ecommerce.dto.*;
import com.ecommerce.entity.*;
import com.ecommerce.feign.BankClient;
import com.ecommerce.kafka.DeliveryEvent;
import com.ecommerce.repository.*;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.CartService;
import com.ecommerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired private UserService userService;
    @Autowired private CartService cartService;
    @Autowired private OrderRepo orderRepo;
    @Autowired private OrderItemRepo orderItemRepo;
    @Autowired private BankClient bankClient;
    @Autowired private CartItemRepo cartItemRepo;
    @Autowired private UserRepo userRepo;
    @Autowired private ProductRepo productRepo;

    // ✅ Single correct KafkaTemplate bean
    @Autowired
    private KafkaTemplate<String, DeliveryEvent> kafkaTemplate;

    private static final String DELIVERY_TOPIC = "delivery-requests";
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    public PurchaseResponse purchaseItems(String userId, String accountNumber, String description) {
        User user = userService.getUserById(userId);
        var cartItems = cartService.getCartItems(userId);

        if (cartItems == null || cartItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty.");
        }

        // ✅ Calculate total
        BigDecimal totalAmount = cartItems.stream()
                .map(ci -> ci.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ✅ Debit from bank
        DebitRequest request = new DebitRequest();
        request.setAccountNumber(accountNumber);
        request.setAmount(totalAmount.doubleValue());
        request.setDescription(description == null || description.isBlank()
                ? "Purchase from E-Commerce"
                : description);
        bankClient.debitAccount(request);

        // ✅ Save order
        Order order = new Order();
        order.setUser(user);
        order.setPurchaseDate(LocalDateTime.now());
        order.setTotalAmount(totalAmount);
        order = orderRepo.save(order);

        // ✅ Save order items
        Order finalOrder = order;
        var orderItems = cartItems.stream().map(ci -> {
            OrderItem oi = new OrderItem();
            oi.setOrder(finalOrder);  // ✅ effectively final
            oi.setProduct(productRepo.findById(ci.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found")));
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(ci.getPrice());
            return oi;
        }).toList();

        orderItemRepo.saveAll(orderItems);

        // ✅ Clear cart
        cartService.clearCart(userId);

        // ✅ Build Kafka DeliveryEvent
        DeliveryEvent event = new DeliveryEvent();
        event.setOrderId(order.getId());
        event.setUserId(userId);
        event.setProductNames(orderItems.stream().map(oi -> oi.getProduct().getName()).toList());
        event.setTotalAmount(totalAmount);
        event.setDeliveryDate(LocalDateTime.now().plusDays(3).toLocalDate().toString()); // random 3 days later

        // ✅ Publish to Kafka
        kafkaTemplate.send(DELIVERY_TOPIC, event);
        log.info("📦 Delivery event published to Kafka: {}", event);

        // ✅ Build response
        PurchaseResponse response = new PurchaseResponse();
        response.setOrderId(order.getId());
        response.setTotalAmount(totalAmount);
        response.setPurchaseDate(order.getPurchaseDate());
        response.setProductNames(event.getProductNames());
        response.setMessage("Order placed successfully & Delivery Scheduled");
        return response;
    }

    @Override
    public List<DashboardResponse> getMonthlyDashboard(String userId) {
        User user = userService.getUserById(userId);
        List<Order> orders = orderRepo.findByUser(user);

        Map<YearMonth, List<Order>> grouped = orders.stream()
                .collect(Collectors.groupingBy(o -> YearMonth.from(o.getPurchaseDate())));

        List<DashboardResponse> result = new ArrayList<>();

        for (Map.Entry<YearMonth, List<Order>> entry : grouped.entrySet()) {
            YearMonth ym = entry.getKey();
            List<Order> monthlyOrders = entry.getValue();

            BigDecimal total = BigDecimal.ZERO;
            Map<String, DashboardItem> itemMap = new LinkedHashMap<>();

            for (Order order : monthlyOrders) {
                List<OrderItem> items = orderItemRepo.findByOrder(order);

                for (OrderItem item : items) {
                    String key = item.getProduct().getName();
                    DashboardItem existing = itemMap.get(key);

                    if (existing != null) {
                        existing.setQuantity(existing.getQuantity() + item.getQuantity());
                    } else {
                        DashboardItem newItem = new DashboardItem();
                        newItem.setName(item.getProduct().getName());
                        newItem.setPrice(item.getPrice());
                        newItem.setQuantity(item.getQuantity());
                        itemMap.put(key, newItem);
                    }

                    total = total.add(item.getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity())));
                }
            }

            DashboardResponse dashboard = new DashboardResponse();
            dashboard.setMonth(ym.toString());
            dashboard.setTotalPurchaseAmount(total);
            dashboard.setItems(new ArrayList<>(itemMap.values()));
            result.add(dashboard);
        }

        return result;
    }

    // --- Legacy methods (optional for backward compatibility) ---

    public PurchaseResponse purchase(DebitRequest request) {
        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<CartItem> cartItems = cartItemRepo.findByUser_Id(request.getUserId());
        if (cartItems == null || cartItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        PurchaseResponse response = new PurchaseResponse();
        response.setOrderId("TEST-ORDER");
        response.setMessage("Order placed successfully");
        response.setTotalAmount(BigDecimal.valueOf(request.getAmount()));
        response.setPurchaseDate(LocalDateTime.now());
        response.setProductNames(cartItems.stream()
                .map(ci -> ci.getProduct().getName()).toList());
        return response;
    }

    public void purchase(String userId, String accountNumber) {
        purchaseItems(userId, accountNumber, "Purchase from E-Commerce");
    }
}
