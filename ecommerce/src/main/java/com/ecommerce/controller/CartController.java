package com.ecommerce.controller;

import com.ecommerce.dto.CartItemRequest;
import com.ecommerce.dto.CartItemResponse;
import com.ecommerce.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    /** Add item(s) to cart */
    @PostMapping("/add")
    public ResponseEntity<String> add(@RequestBody CartItemRequest request) {
        log.info("Adding product {} (qty={}) to cart for user {}",
                request.getProductId(), request.getQuantity(), request.getUserId());

        String result = cartService.addToCart(request);
        return ResponseEntity.ok(result);
    }

    /** Remove specific item from cart */
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> remove(@PathVariable String id) {   // ✅ String ID for Mongo
        log.info("Removing cart item with id={}", id);
        cartService.removeFromCart(id);
        return ResponseEntity.ok("Item removed from cart");
    }

    /** Get all items in a user’s cart */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CartItemResponse>> getUserCart(@PathVariable String userId) {
        log.debug("Fetching cart items for user {}", userId);
        List<CartItemResponse> items = cartService.getCartItems(userId);
        return ResponseEntity.ok(items);
    }

    /** Clear entire cart for a user */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> clearCartByUser(@PathVariable String userId) {
        log.info("Clearing cart for userId={}", userId);
        cartService.clearCart(userId);
        return ResponseEntity.ok("Cart cleared successfully");
    }
}
