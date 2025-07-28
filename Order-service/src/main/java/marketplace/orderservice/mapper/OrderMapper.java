package marketplace.orderservice.mapper;

import marketplace.orderservice.dto.OrderItemResponseDto;
import marketplace.orderservice.dto.OrderResponseDto;
import marketplace.orderservice.entity.Order;
import marketplace.orderservice.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponseDto toOrderResponseDto(Order order) {
        if (order == null) {
            return null;
        }

        List<OrderItemResponseDto> itemDtos = order.getItems() != null
            ? order.getItems().stream()
                .map(this::toOrderItemResponseDto)
                .collect(Collectors.toList())
            : List.of();

        return OrderResponseDto.builder()
                .id(order.getId())
                .buyerId(order.getBuyerId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(itemDtos)
                .build();
    }

    public OrderItemResponseDto toOrderItemResponseDto(OrderItem item) {
        if (item == null) {
            return null;
        }

        return OrderItemResponseDto.builder()
                .id(item.getId())
                .productName(item.getProductName())
                .productPrice(item.getProductPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}
