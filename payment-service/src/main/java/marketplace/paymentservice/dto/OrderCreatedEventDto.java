package marketplace.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEventDto(
        UUID orderId,
        UUID buyerId,
        BigDecimal totalAmount,
        String status,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,
        List<OrderItemEventDto> items
) {

    public record OrderItemEventDto(
            String productName,
            BigDecimal productPrice,
            Integer quantity,
            BigDecimal totalPrice
    ) {}
}
