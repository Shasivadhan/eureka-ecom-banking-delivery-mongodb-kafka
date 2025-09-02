package com.ecommerce.service;

import com.ecommerce.dto.CartItemRequest;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartItemRepo;
import com.ecommerce.repository.ProductRepo;
import com.ecommerce.repository.UserRepo;
import com.ecommerce.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {

    @Mock private CartItemRepo cartItemRepo;
    @Mock private ProductRepo productRepo;
    @Mock private UserRepo userRepo;

    @InjectMocks
    private CartServiceImpl cartService;

    private CartItemRequest request;
    private User user;
    private Product product;

    // ✅ Mongo-style string IDs
    private final String USER_ID = "507f1f77bcf86cd799439061";
    private final String PRODUCT_ID = "507f1f77bcf86cd799439062";
    private final String CART_ITEM_ID = "507f1f77bcf86cd799439063";

    @BeforeEach
    public void setup() {
        request = new CartItemRequest();
        request.setUserId(USER_ID);
        request.setProductId(PRODUCT_ID);
        request.setQuantity(2);

        user = new User();
        user.setId(USER_ID);
        user.setFullName("Test User");

        product = new Product();
        product.setId(PRODUCT_ID);
        product.setName("Keyboard");
        product.setPrice(BigDecimal.valueOf(500));
        product.setStock(10);
    }

    @Test
    public void testAddToCart_Success() {
        when(userRepo.findById(USER_ID)).thenReturn(Optional.of(user));
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(cartItemRepo.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = cartService.addToCart(request);

        assertTrue(result.contains("Added")); // ✅ message contains "Added ... to cart"
        verify(cartItemRepo, times(1)).save(any(CartItem.class));
    }

    @Test
    public void testAddToCart_UserNotFound() {
        when(userRepo.findById(USER_ID)).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () ->
                cartService.addToCart(request));

        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    public void testAddToCart_ProductNotFound() {
        when(userRepo.findById(USER_ID)).thenReturn(Optional.of(user));
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, () ->
                cartService.addToCart(request));

        assertTrue(ex.getMessage().contains("Product not found"));
    }

    @Test
    public void testGetCartItemsByUserId_Empty() {
        when(cartItemRepo.findByUser_Id(USER_ID)).thenReturn(List.of());

        assertTrue(cartService.getCartItems(USER_ID).isEmpty());
    }

    @Test
    public void testClearCart() {
        doNothing().when(cartItemRepo).deleteByUser_Id(USER_ID);

        cartService.clearCart(USER_ID);

        verify(cartItemRepo, times(1)).deleteByUser_Id(USER_ID);
    }
}
