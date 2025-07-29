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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @PreAuthorize("hasRole('admin_client_role') or hasRole('buyer_client_role')")
    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<List<Order>>> getMyOrders (@AuthenticationPrincipal Jwt jwt ){
        UUID userId = UUID.fromString(jwt.getSubject());
        ApiResponseDTO<List<Order>> response = orderService.getMyOrders(userId);
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasRole('admin_client_role')")
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllOrders(){
        ApiResponseDTO<List<Order>> response = orderService.getAllOrders();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('admin_client_role') or hasRole('buyer_client_role')")
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponseDTO<Order>> getOrderById(@PathVariable UUID orderId,
                                                              @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        ApiResponseDTO<Order> response = orderService.getOrderById(orderId, userId);
        return ResponseEntity.ok(response);
    }

}
