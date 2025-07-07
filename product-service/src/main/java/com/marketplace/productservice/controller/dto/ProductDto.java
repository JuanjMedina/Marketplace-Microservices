package com.marketplace.productservice.controller.dto;

import com.marketplace.productservice.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "User data transfer object for creating product")
public record ProductDto(

        @Schema(description = "Name for the product", example = "Potato", required = true)
        @NotBlank(message = "Product name cannot be blank")
        @Size(min = 3, max = 50, message = "Product name must be between 3 and 50 characters")
        String name,

        @Schema(description = "Description for the product", example = "potato with tomato", required = true)
        @NotBlank(message = "Product description cannot be blank")
        @Size(min = 3, max = 100, message = "Product description must be between 3 and 100 characters")
        String description,

        @Schema(description = "The price that you want to sell", example = "$10.22", required = true)
        @NotNull(message = "Price cannot be null")
        @DecimalMin(value = "0.01", inclusive = true, message = "Price must be greater than 0")
        BigDecimal price,

        @Schema(description = "The quantity that you have ", example = "12", required = true)
        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity

) {
    public static Product mapToProduct(ProductDto productDto, String sellerId) {
        return Product.builder()
                .name(productDto.name())
                .sellerId(sellerId)
                .description(productDto.description())
                .price(productDto.price())
                .quantity(productDto.quantity())
                .build();
    }

}