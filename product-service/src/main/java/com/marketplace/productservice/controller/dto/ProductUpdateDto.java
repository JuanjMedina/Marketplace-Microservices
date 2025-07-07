package com.marketplace.productservice.controller.dto;

import com.marketplace.productservice.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "DTO para actualizar información de un producto")
public record ProductUpdateDto(
        @Schema(description = "Nombre del producto",
                example = "iPhone 15 Pro",
                minLength = 3,
                maxLength = 50)
        @NotBlank(message = "Product name cannot be blank")
        @Size(min = 3, max = 50, message = "Product name must be between 3 and 50 characters")
        String name,

        @Schema(description = "Descripción detallada del producto",
                example = "Smartphone Apple con pantalla de 6.1 pulgadas y chip A17 Pro",
                minLength = 3,
                maxLength = 100)
        @NotBlank(message = "Product description cannot be blank")
        @Size(min = 3, max = 100, message = "Product description must be between 3 and 100 characters")
        String description,

        @Schema(description = "Precio del producto en euros",
                example = "999.99",
                minimum = "0.01")
        @NotNull(message = "Price cannot be null")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        BigDecimal price,

        @Schema(description = "Cantidad disponible en inventario",
                example = "25",
                minimum = "1")
        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
) {
    public static Product mapToProduct(ProductUpdateDto productDto) {
        return Product.builder()
                .name(productDto.name())
                .description(productDto.description())
                .price(productDto.price())
                .quantity(productDto.quantity())
                .build();
    }
}
