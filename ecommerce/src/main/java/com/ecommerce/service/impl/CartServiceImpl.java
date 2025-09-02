package com.ecommerce.service.impl;

import com.ecommerce.dto.CartItemRequest;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartItemRepo;
import com.ecommerce.repository.ProductRepo;
import com.ecommerce.repository.UserRepo;
import com.ecommerce.service.CartService;
import org.springframework.stereotype.Service;
import com.ecommerce.dto.CartItemResponse;


import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartItemRepo cartItemRepo;
    private final ProductRepo productRepo;
    private final UserRepo userRepo;

    public CartServiceImpl(CartItemRepo cartItemRepo, ProductRepo productRepo, UserRepo userRepo) {
        this.cartItemRepo = cartItemRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    @Override
    public String addToCart(CartItemRequest request) {
        String userId = request.getUserId();
        String productId = request.getProductId();
        int qty = request.getQuantity() <= 0 ? 1 : request.getQuantity();

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        BigDecimal price = product.getPrice();
        if (price == null) {
            throw new IllegalStateException("Product price is not set for productId=" + productId);
        }

        // Find existing cart item or create new
        CartItem item = cartItemRepo.findByUser_IdAndProduct_Id(userId, productId)
                .orElseGet(() -> {
                    CartItem ci = new CartItem();
                    ci.setUser(user);
                    ci.setProduct(product);
                    ci.setQuantity(0);
                    return ci;
                });

        item.setQuantity(item.getQuantity() + qty);
        item.setUnitPrice(price); // always set from Product

        cartItemRepo.save(item);
        return "Added " + qty + " item(s) to cart for user " + userId + " (product " + productId + ").";
    }

    @Override
    public List<CartItemResponse> getCartItems(String userId) {
        List<CartItem> cartItems = cartItemRepo.findByUser_Id(userId);  // ✅ use _Id method

        return cartItems.stream().map(item -> {
            CartItemResponse dto = new CartItemResponse();
            dto.setCartItemId(item.getId());
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getName());
            dto.setCategory(item.getProduct().getCategory());
            dto.setPrice(item.getProduct().getPrice());
            dto.setQuantity(item.getQuantity());
            dto.setTotal(item.getProduct().getPrice()
                    .multiply(new java.math.BigDecimal(item.getQuantity())));
            return dto;
        }).toList();
    }


    @Override
    public void clearCart(String userId) {
        cartItemRepo.deleteByUser_Id(userId);
    }

    @Override
    public void removeFromCart(String cartItemId) {
        cartItemRepo.deleteById(cartItemId);
    }
}
