package marketplace.orderservice.dto;

import lombok.Builder;
import marketplace.orderservice.entity.OrderStaus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderResponseDto(
        UUID id,
        UUID buyerId,
        OrderStaus status,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<OrderItemResponseDto> items
) {
}
