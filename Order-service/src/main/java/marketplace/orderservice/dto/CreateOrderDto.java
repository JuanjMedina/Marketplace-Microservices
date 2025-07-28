package marketplace.orderservice.dto;

import java.util.List;

public record CreateOrderDto(
        List<OrderItemDto> items
) {
}
