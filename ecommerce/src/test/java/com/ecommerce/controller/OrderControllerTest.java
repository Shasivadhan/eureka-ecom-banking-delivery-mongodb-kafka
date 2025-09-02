package com.ecommerce.controller;

import com.ecommerce.dto.PurchaseRequest;
import com.ecommerce.dto.PurchaseResponse;
import com.ecommerce.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testPurchase_Success() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setUserId("507f1f77bcf86cd799439011"); // ✅ String for Mongo
        request.setAccountNumber("1234567890");
        request.setDescription("Test Purchase");

        PurchaseResponse mockResponse = new PurchaseResponse();
        mockResponse.setOrderId("507f1f77bcf86cd799439099"); // ✅ String orderId
        mockResponse.setTotalAmount(new BigDecimal("100.00"));
        mockResponse.setPurchaseDate(LocalDateTime.of(2025, 7, 19, 17, 0));
        mockResponse.setProductNames(Collections.singletonList("Monitor"));
        mockResponse.setMessage("Order placed successfully");

        when(orderService.purchaseItems(anyString(), anyString(), anyString()))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/orders/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("507f1f77bcf86cd799439099"))
                .andExpect(jsonPath("$.totalAmount").value(100.00))
                .andExpect(jsonPath("$.productNames[0]").value("Monitor"))
                .andExpect(jsonPath("$.message").value("Order placed successfully"));
    }

    @Test
    public void testPurchase_EmptyCart() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setUserId("507f1f77bcf86cd799439012");
        request.setAccountNumber("0000");
        request.setDescription("Empty Cart");

        when(orderService.purchaseItems(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Cart is empty"));

        mockMvc.perform(post("/api/orders/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cart is empty"));
    }

    @Test
    public void testPurchase_BankDecline() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setUserId("507f1f77bcf86cd799439013");
        request.setAccountNumber("9999");
        request.setDescription("Bank Error");

        when(orderService.purchaseItems(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Bank declined"));

        mockMvc.perform(post("/api/orders/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bank declined"));
    }

    @Test
    public void testGetMonthlyDashboard_Empty() throws Exception {
        String userId = "507f1f77bcf86cd799439014";

        when(orderService.getMonthlyDashboard(userId))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/orders/dashboard/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
