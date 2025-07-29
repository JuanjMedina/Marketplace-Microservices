package marketplace.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

@Schema(description = "Data transfer object for order item details")
public record OrderItemDto(
        @Schema(
            description = "Unique identifier of the product to order",
            required = true,
            example = "123e4567-e89b-12d3-a456-426614174000"
        )
        @NotNull(message = "Product ID cannot be null")
        UUID productId,

        @Schema(
            description = "Quantity of the product to order",
            required = true,
            example = "2",
            minimum = "1"
        )
        @NotNull(message = "Quantity cannot be null")
        @Positive(message = "Quantity must be positive")
        Integer quantity
) {
}
