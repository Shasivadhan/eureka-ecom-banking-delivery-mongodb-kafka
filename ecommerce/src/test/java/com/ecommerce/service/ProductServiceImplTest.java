package com.ecommerce.service;

import com.ecommerce.dto.ProductResponse;
import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepo;
import com.ecommerce.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepo productRepo;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product p1, p2;

    // ✅ Use String IDs for Mongo
    private final String P1_ID = "507f1f77bcf86cd799439071";
    private final String P2_ID = "507f1f77bcf86cd799439072";

    @BeforeEach
    public void setup() {
        p1 = new Product();
        p1.setId(P1_ID);
        p1.setName("Laptop");
        p1.setPrice(BigDecimal.valueOf(75000));

        p2 = new Product();
        p2.setId(P2_ID);
        p2.setName("Phone");
        p2.setPrice(BigDecimal.valueOf(30000));
    }

    @Test
    public void testGetAllProducts() {
        when(productRepo.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<ProductResponse> products = productService.getAllProducts();

        assertEquals(2, products.size());
        assertEquals("Laptop", products.get(0).getName());
        assertEquals("Phone", products.get(1).getName());
        assertEquals(P1_ID, products.get(0).getId()); // ✅ String ID check
        assertEquals(P2_ID, products.get(1).getId());
    }

    @Test
    public void testSearchProducts_Found() {
        when(productRepo.findByNameContainingIgnoreCase("lap")).thenReturn(List.of(p1));

        List<ProductResponse> result = productService.searchProducts("lap");

        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
        assertEquals(P1_ID, result.get(0).getId()); // ✅ String ID check
    }

    @Test
    public void testSearchProducts_Empty() {
        when(productRepo.findByNameContainingIgnoreCase("notfound")).thenReturn(List.of());

        List<ProductResponse> result = productService.searchProducts("notfound");

        assertTrue(result.isEmpty());
    }
}
