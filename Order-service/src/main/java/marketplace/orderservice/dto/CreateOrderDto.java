package marketplace.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "Data transfer object for creating a new order")
public record CreateOrderDto(
        @Schema(
            description = "List of items to include in the order",
            required = true,
            example = "[{\"productId\": \"123e4567-e89b-12d3-a456-426614174000\", \"quantity\": 2}]"
        )
        @NotEmpty(message = "Order must contain at least one item")
        @Valid
        List<OrderItemDto> items
) {
}
