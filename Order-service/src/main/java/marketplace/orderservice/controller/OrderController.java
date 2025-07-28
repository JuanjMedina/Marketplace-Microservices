package marketplace.orderservice.controller;

import jakarta.validation.Valid;
import marketplace.orderservice.dto.ApiResponseDTO;
import marketplace.orderservice.dto.CreateOrderDto;
import marketplace.orderservice.dto.OrderResponseDto;
import marketplace.orderservice.entity.Order;
import marketplace.orderservice.mapper.OrderMapper;
import marketplace.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @PostMapping()
    public ResponseEntity<ApiResponseDTO<OrderResponseDto>> createOrder(@Valid @RequestBody CreateOrderDto orderDto,
                                           @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        ApiResponseDTO<Order> response = orderService.createOrder(orderDto, userId);

        // Convert the Order entity to OrderResponseDto to avoid circular reference
        OrderResponseDto orderResponse = orderMapper.toOrderResponseDto(response.data());

        // Return using ApiResponseDTO wrapper
        ApiResponseDTO<OrderResponseDto> apiResponse = ApiResponseDTO.<OrderResponseDto>builder()
                .success(response.success())
                .message(response.message())
                .data(orderResponse)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

}
