package com.marketplace.productservice.service;

import com.marketplace.productservice.controller.dto.ApiResponseDTO;
import com.marketplace.productservice.entity.Product;
import com.marketplace.productservice.exception.ProductNotFoundException;
import com.marketplace.productservice.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;

    @Override
    public ApiResponseDTO<List<Product>> getAllProducts() {
        // This method should return a list of all products.
        List<Product> products = productRepository.findAll();

        return ApiResponseDTO.<List<Product>>builder()
                .data(products)
                .message(products.isEmpty() ? "No products found" : "Products retrieved successfully")
                .success(true)
                .build();
    }

    @Override
    public ApiResponseDTO<Product> getProductById(String id) {
        //This method should return a product by ID.
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product not found with ID: " + id, HttpStatus.NOT_FOUND)
        );

        // If the product is not found, throw a ProductNotFoundException.

        // If the product is found, return it wrapped in an ApiResponseDTO.
        return ApiResponseDTO.<Product>builder()
                .message("Product retrieved successfully")
                .success(true)
                .data(product)
                .build();
    }

    @Override
    @PreAuthorize("hasRole('') or hasRole('ROLE_ADMIN')")
    public ApiResponseDTO<Product> createProduct(@NonNull Product product) {
        // This method should create a new product.

        Product saveProduct = productRepository.save(product);

        // If the product is created successfully, return it wrapped in an ApiResponseDTO.
        return ApiResponseDTO.<Product>builder()
                .message("Product created successfully")
                .success(true)
                .data(saveProduct)
                .build();

    }

    @Override
    public ApiResponseDTO<Product> updateProduct(String id, Product product) {
        return null;
    }

    @Override
    public ApiResponseDTO<Void> deleteProduct(String id) {
        // This method should delete a product by ID.
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product not found with ID: " + id, HttpStatus.NOT_FOUND)
        );

        productRepository.delete(product);


        return ApiResponseDTO.<Void>builder()
                .message("Product deleted successfully")
                .success(true)
                .data(null)
                .build();
    }
}
