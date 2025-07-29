package marketplace.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import marketplace.orderservice.entity.OrderStaus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Response data transfer object for order information")
@Builder
public record OrderResponseDto(
        @Schema(
            description = "Unique identifier of the order",
            example = "123e4567-e89b-12d3-a456-426614174000"
        )
        UUID id,

        @Schema(
            description = "Unique identifier of the buyer who placed the order",
            example = "456e7890-e89b-12d3-a456-426614174001"
        )
        UUID buyerId,

        @Schema(
            description = "Current status of the order",
            example = "PENDING",
            allowableValues = {"PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"}
        )
        OrderStaus status,

        @Schema(
            description = "Total amount of the order",
            example = "299.98"
        )
        BigDecimal totalAmount,

        @Schema(
            description = "Timestamp when the order was created",
            example = "2024-01-15T10:30:00"
        )
        LocalDateTime createdAt,

        @Schema(
            description = "Timestamp when the order was last updated",
            example = "2024-01-15T10:30:00"
        )
        LocalDateTime updatedAt,

        @Schema(
            description = "List of items included in the order"
        )
        List<OrderItemResponseDto> items
) {
}
