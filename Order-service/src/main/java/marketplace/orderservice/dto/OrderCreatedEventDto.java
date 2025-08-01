package marketplace.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import marketplace.orderservice.entity.Order;
import marketplace.orderservice.entity.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderCreatedEventDto(
        UUID orderId,
        UUID buyerId,
        BigDecimal totalAmount,
        String status,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,
        List<OrderItemEventDto> items
) {

    @Builder
    public record OrderItemEventDto(
            String productName,
            BigDecimal productPrice,
            Integer quantity,
            BigDecimal totalPrice
    ) {}

    public static OrderCreatedEventDto fromOrder(Order order) {
        List<OrderItemEventDto> itemEvents = order.getItems().stream()
                .map(item -> OrderItemEventDto.builder()
                        .productName(item.getProductName())
                        .productPrice(item.getProductPrice())
                        .quantity(item.getQuantity())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .toList();

        return OrderCreatedEventDto.builder()
                .orderId(order.getId())
                .buyerId(order.getBuyerId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .items(itemEvents)
                .build();
    }
}
