package com.ecommerce.service.impl;

import com.ecommerce.dto.CartItemResponse;
import com.ecommerce.dto.PurchaseResponse;
import com.ecommerce.dto.DebitRequest;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.feign.BankClient;
import com.ecommerce.repository.OrderItemRepo;
import com.ecommerce.repository.OrderRepo;
import com.ecommerce.service.CartService;
import com.ecommerce.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private UserService userService;
    @Mock private CartService cartService;
    @Mock private OrderRepo orderRepo;
    @Mock private OrderItemRepo orderItemRepo;
    @Mock private BankClient bankClient;

    @InjectMocks private OrderServiceImpl orderService;

    private User user;
    private Product product;
    private CartItem cartItem;
    private Order order;

    // ✅ Use Mongo-style string IDs
    private final String USER_ID = "507f1f77bcf86cd799439041";
    private final String PRODUCT_ID = "507f1f77bcf86cd799439042";
    private final String CART_ITEM_ID = "507f1f77bcf86cd799439043";
    private final String ORDER_ID = "507f1f77bcf86cd799439044";

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(USER_ID);

        product = new Product();
        product.setId(PRODUCT_ID);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("15000.00"));

        // CartItemResponse instead of CartItem
        CartItemResponse cartItemResponse = new CartItemResponse();
        cartItemResponse.setCartItemId(CART_ITEM_ID);
        cartItemResponse.setProductId(PRODUCT_ID);
        cartItemResponse.setProductName("Laptop");
        cartItemResponse.setCategory("Electronics");
        cartItemResponse.setPrice(new BigDecimal("15000.00"));
        cartItemResponse.setQuantity(2); // total = 15000 * 2
        cartItemResponse.setTotal(new BigDecimal("30000.00"));

        order = new Order();
        order.setUser(user);
        order.setPurchaseDate(LocalDateTime.now());
        order.setTotalAmount(new BigDecimal("30000.00"));

        when(userService.getUserById(USER_ID)).thenReturn(user);
        when(cartService.getCartItems(USER_ID)).thenReturn(List.of(cartItemResponse));
        when(bankClient.debitAccount(any(DebitRequest.class))).thenReturn("OK");

        // return saved order with id
        when(orderRepo.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(ORDER_ID);
            return o;
        });
    }


    @Test
    void testPurchaseItems_Success() {
        PurchaseResponse resp = orderService.purchaseItems(USER_ID, "1234567890", "Test Purchase");

        assertNotNull(resp);
        assertEquals(ORDER_ID, resp.getOrderId()); // ✅ String id
        assertEquals(0, resp.getTotalAmount().compareTo(new BigDecimal("30000.00")));
        assertEquals("Order placed successfully", resp.getMessage());

        verify(orderItemRepo, times(1)).saveAll(anyList());
        verify(cartService, times(1)).clearCart(USER_ID);
    }
}
