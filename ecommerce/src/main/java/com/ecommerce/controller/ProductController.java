package com.ecommerce.controller;

import com.ecommerce.dto.ProductResponse;
import com.ecommerce.service.ProductService;

import org.slf4j.Logger;                                      // <–– added
import org.slf4j.LoggerFactory;                               // <–– added
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger log =
            LoggerFactory.getLogger(ProductController.class);      // <–– added

    @Autowired
    private ProductService productService;

    @GetMapping("/SearchProducts")
    public List<ProductResponse> search(@RequestParam(required = false) String keyword) {
        log.info("Searching products with keyword={}", keyword);                     // <–– added
        List<ProductResponse> results = productService.searchProducts(keyword);
        log.debug("searchProducts returned {} results for keyword={}",               // <–– added
                results.size(), keyword);
        return results;
    }

    @GetMapping("/Inventory")
    public List<ProductResponse> getAll() {
        log.info("Fetching all products");                                          // <–– added
        List<ProductResponse> list = productService.getAllProducts();
        log.debug("getAllProducts returned {} items", list.size());                  // <–– added
        return list;
    }
}
