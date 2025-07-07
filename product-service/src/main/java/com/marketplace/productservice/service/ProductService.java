package com.marketplace.productservice.service;

import com.marketplace.productservice.controller.dto.ApiResponseDTO;
import com.marketplace.productservice.controller.dto.ProductFilterCriteria;
import com.marketplace.productservice.entity.Product;
import com.marketplace.productservice.exception.ProductNotFoundException;
import com.marketplace.productservice.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;

    @Override
    public ApiResponseDTO<Page<Product>> getAllProducts(ProductFilterCriteria filters, Pageable page) {

        Specification<Product> spec = ProductSpecification.findByCriteria(filters);

        // This method should return a list of all products.
        Page<Product> products = productRepository.findAll(spec, page);

        return ApiResponseDTO.<Page<Product>>builder()
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

    @Override
    public ApiResponseDTO<Product> updatePartialProduct(String id, Product product) {
        Optional<Product> productUpdate = productRepository.findById(id);

        if (productUpdate.isEmpty()) {
            throw new ProductNotFoundException("Product not found with ID: " + id, HttpStatus.NOT_FOUND);
        }

        if (product.getName() != null) {
            productUpdate.get().setName(product.getName());
        }
        if (product.getDescription() != null) {
            productUpdate.get().setDescription(product.getDescription());
        }
        if (product.getPrice() != null) {
            productUpdate.get().setPrice(product.getPrice());
        }
        if (product.getQuantity() != null) {
            productUpdate.get().setQuantity(product.getQuantity());
        }

        productRepository.save(productUpdate.get());


        return ApiResponseDTO.<Product>builder()
                .message("Product updated successfully")
                .success(true)
                .data(productUpdate.get())
                .build();
    }


}
