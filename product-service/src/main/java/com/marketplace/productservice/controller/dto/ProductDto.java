package com.marketplace.productservice.controller.dto;

import com.marketplace.productservice.entity.Product;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductDto(

        @NotBlank(message = "Product name cannot be blank")
        @Size(min = 3, max = 50, message = "Product name must be between 3 and 50 characters")
        String name,

        @NotBlank(message = "Product description cannot be blank")
        @Size(min = 3, max = 100, message = "Product description must be between 3 and 100 characters")
        String description,

        @NotNull(message = "Price cannot be null")
        @DecimalMin(value = "0.01", inclusive = true, message = "Price must be greater than 0")
        BigDecimal price,

        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity

) {
    public static Product mapToProduct(ProductDto productDto ) {
        return Product.builder()
                .name(productDto.name())
                .description(productDto.description())
                .price(productDto.price())
                .quantity(productDto.quantity())
                .build();
    }

}