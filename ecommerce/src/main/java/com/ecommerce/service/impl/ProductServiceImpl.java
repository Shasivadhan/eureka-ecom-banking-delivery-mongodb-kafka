package com.ecommerce.service.impl;

import com.ecommerce.dto.ProductResponse;
import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepo;
import com.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepo productRepo;  // extends MongoRepository<Product, String>

    @Override
    public List<ProductResponse> searchProducts(String keyword) {
        final List<Product> products =
                (keyword == null || keyword.trim().isEmpty())
                        ? productRepo.findAll()
                        : productRepo.findByNameContainingIgnoreCase(keyword.trim());

        return products.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepo.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse mapToResponse(Product p) {
        ProductResponse res = new ProductResponse();
        res.setId(p.getId());   // Mongo _id is String
        res.setName(p.getName());
        res.setCategory(p.getCategory());

        // Null-safe price & stock
        BigDecimal price = p.getPrice() != null ? p.getPrice() : BigDecimal.ZERO;
        Integer stock = p.getStock() != null ? p.getStock() : 0;

        res.setPrice(price);
        res.setStock(stock);

        return res;
    }
}
