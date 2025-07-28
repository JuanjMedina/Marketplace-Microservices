package marketplace.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record OrderItemDto(
        @NotNull(message = "Product ID cannot be null")
        UUID productId,
        @NotNull(message = "Quantity cannot be null")
        @Positive(message = "Quantity must be positive")
        Integer quantity
) {
}
