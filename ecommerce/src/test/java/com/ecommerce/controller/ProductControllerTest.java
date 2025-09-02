package com.ecommerce.controller;

import com.ecommerce.dto.ProductResponse;
import com.ecommerce.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import java.util.Arrays;

import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetAllProducts() throws Exception {
        // ✅ Use String ids instead of Long
        ProductResponse p1 = new ProductResponse("507f1f77bcf86cd799439021", "Laptop", BigDecimal.valueOf(75000));
        ProductResponse p2 = new ProductResponse("507f1f77bcf86cd799439022", "Phone", BigDecimal.valueOf(30000));

        List<ProductResponse> productList = Arrays.asList(p1, p2);

        when(productService.getAllProducts()).thenReturn(productList);

        mockMvc.perform(get("/api/products/Inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].price").value(30000));
    }

    @Test
    public void testSearchProductByName() throws Exception {
        String query = "Phone";

        ProductResponse phone = new ProductResponse();
        phone.setId("507f1f77bcf86cd799439023"); // ✅ String id
        phone.setName("Phone");
        phone.setPrice(BigDecimal.valueOf(30000));

        List<ProductResponse> result = List.of(phone);

        when(productService.searchProducts(query)).thenReturn(result);

        mockMvc.perform(get("/api/products/SearchProducts")
                        .param("keyword", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Phone"));
    }
}
