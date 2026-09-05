package com.fincommerce.controller;

import com.fincommerce.dto.ApiResponse;
import com.fincommerce.dto.ProductDtos;
import com.fincommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<ProductDtos.CategoryDto>>> getCategories() {
        List<ProductDtos.CategoryDto> list = productService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved", list));
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductDtos.ProductDto>>> searchProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId) {
        List<ProductDtos.ProductDto> list = productService.searchProducts(query, categoryId);
        return ResponseEntity.ok(ApiResponse.success("Products retrieved", list));
    }

    @GetMapping("/products/featured")
    public ResponseEntity<ApiResponse<List<ProductDtos.ProductDto>>> getFeaturedProducts() {
        List<ProductDtos.ProductDto> list = productService.getFeaturedProducts();
        return ResponseEntity.ok(ApiResponse.success("Featured products retrieved", list));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductDtos.ProductDto>> getProductById(@PathVariable Long id) {
        ProductDtos.ProductDto product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success("Product retrieved", product));
    }
}
