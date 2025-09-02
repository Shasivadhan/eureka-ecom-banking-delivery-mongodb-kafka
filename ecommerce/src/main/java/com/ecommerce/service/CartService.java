package com.ecommerce.service;

import com.ecommerce.dto.CartItemRequest;
import com.ecommerce.dto.CartItemResponse;

import java.util.List;

public interface CartService {
    String addToCart(CartItemRequest request);

    /** Primary API */
    List<CartItemResponse> getCartItems(String userId);   // ✅ return DTOs

    /** Alias to satisfy tests that call getCartItemsByUserId(...) */
    default List<CartItemResponse> getCartItemsByUserId(String userId) {  // ✅ updated
        return getCartItems(userId);
    }

    void clearCart(String userId);

    void removeFromCart(String cartItemId);
}
