package com.ecommerce.service;

import com.ecommerce.dto.ProductResponse;
import java.util.List;

public interface ProductService {
    List<ProductResponse> searchProducts(String keyword);
    List<ProductResponse> getAllProducts();
}
