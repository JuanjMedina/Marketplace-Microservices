package com.marketplace.productservice.controller;

import com.marketplace.productservice.controller.dto.ApiResponseDTO;
import com.marketplace.productservice.controller.dto.ProductDto;
import com.marketplace.productservice.entity.Product;
import com.marketplace.productservice.service.IProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {

    private final IProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<Product>>> getAllProducts() {
        ApiResponseDTO<List<Product>> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponseDTO<Product>> getProductById(@PathVariable("productId") String id) {
        ApiResponseDTO<Product> product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PreAuthorize("hasRole('admin_client_role') or hasRole('seller_client_role')")
    @PostMapping
    ResponseEntity<ApiResponseDTO<Product>> createProduct(@Valid @RequestBody ProductDto productDto, @AuthenticationPrincipal Jwt jwt) {
        // Extract the seller ID from the JWT token
        String sellerId = jwt.getClaim("sub");
        Product mapToProduct = ProductDto.mapToProduct(productDto, sellerId);
        ApiResponseDTO<Product> createdProduct = productService.createProduct(mapToProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteProduct(@PathVariable("productId") String id) {
        ApiResponseDTO<Void> response = productService.deleteProduct(id);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponseDTO<Product>> updateProduct(@PathVariable("productId") String id, @RequestBody ProductDto productDto) {
        Product mapToProduct = ProductDto.mapToProduct(productDto, null);
        ApiResponseDTO<Product> updatedProduct = productService.updateProduct(id, mapToProduct);
        return ResponseEntity.ok(updatedProduct);
    }
}
