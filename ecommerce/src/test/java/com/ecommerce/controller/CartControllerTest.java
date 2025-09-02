package com.ecommerce.controller;

import com.ecommerce.dto.CartItemRequest;
import com.ecommerce.dto.CartItemResponse;
import com.ecommerce.entity.CartItem;
import com.ecommerce.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testAddToCart_Success() throws Exception {
        CartItemRequest request = new CartItemRequest();
        request.setUserId("507f1f77bcf86cd799439011");   // ✅ String instead of Long
        request.setProductId("507f1f77bcf86cd799439012"); // ✅ String instead of Long
        request.setQuantity(2);

        when(cartService.addToCart(any(CartItemRequest.class)))
                .thenReturn("Item added to cart");

        mockMvc.perform(post("/api/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Item added to cart"));

        verify(cartService).addToCart(any(CartItemRequest.class));
    }

    @Test
    void testRemoveFromCart_Success() throws Exception {
        String cartItemId = "507f1f77bcf86cd799439013";  // ✅ String id

        doNothing().when(cartService).removeFromCart(cartItemId);

        mockMvc.perform(delete("/api/cart/remove/{id}", cartItemId))
                .andExpect(status().isOk())
                .andExpect(content().string("Item removed from cart"));

        verify(cartService).removeFromCart(cartItemId);
        verifyNoMoreInteractions(cartService);
    }

    @Test
    void testGetCartItemsByUserId_Empty() throws Exception {
        String userId = "507f1f77bcf86cd799439014";

        when(cartService.getCartItems(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/cart/user/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(cartService).getCartItems(userId);
    }

    @Test
    void testGetCartItemsByUserId_WithItems() throws Exception {
        String userId = "507f1f77bcf86cd799439015";

        CartItemResponse response = new CartItemResponse();
        response.setCartItemId("507f1f77bcf86cd799439016");  // ✅ matches DTO field
        response.setProductId("507f1f77bcf86cd799439012");
        response.setProductName("Tablet");
        response.setCategory("Electronics");
        response.setPrice(new BigDecimal("20000"));
        response.setQuantity(3);
        response.setTotal(new BigDecimal("60000"));

        when(cartService.getCartItems(userId)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/cart/user/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cartItemId").value("507f1f77bcf86cd799439016")) // ✅ expect DTO field
                .andExpect(jsonPath("$[0].quantity").value(3));

        verify(cartService).getCartItems(userId);
    }


    @Test
    void testClearCartByUserId() throws Exception {
        String userId = "507f1f77bcf86cd799439017";

        doNothing().when(cartService).clearCart(userId);

        mockMvc.perform(delete("/api/cart/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("Cart cleared"));

        verify(cartService).clearCart(userId);
        verifyNoMoreInteractions(cartService);
    }
}
