package com.marketplace.productservice.controller;

import com.marketplace.productservice.controller.dto.ApiResponseDTO;
import com.marketplace.productservice.controller.dto.ProductDto;
import com.marketplace.productservice.entity.Product;
import com.marketplace.productservice.service.IProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    @PostMapping
    ResponseEntity<ApiResponseDTO<Product>> createProduct(@RequestBody ProductDto productDto) {
        Product mapToProduct = ProductDto.mapToProduct(productDto);
        ApiResponseDTO<Product> createdProduct = productService.createProduct(mapToProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);

    }

    @DeleteMapping("/{produtId}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteProduct(@PathVariable("produtId") String id) {
        ApiResponseDTO<Void> response = productService.deleteProduct(id);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponseDTO<Product>> updateProduct(@PathVariable("productId") String id, @RequestBody ProductDto productDto) {
        Product mapToProduct = ProductDto.mapToProduct(productDto);
        ApiResponseDTO<Product> updatedProduct = productService.updateProduct(id, mapToProduct);
        return ResponseEntity.ok(updatedProduct);
    }
}
