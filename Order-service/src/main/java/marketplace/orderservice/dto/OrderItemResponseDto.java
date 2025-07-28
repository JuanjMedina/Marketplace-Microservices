package marketplace.orderservice.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderItemResponseDto(
        UUID id,
        String productName,
        BigDecimal productPrice,
        Integer quantity,
        BigDecimal totalPrice
) {
}
