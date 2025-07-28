package marketplace.orderservice.controller;

import marketplace.orderservice.dto.ApiResponseDTO;
import marketplace.orderservice.dto.CreateOrderDto;
import marketplace.orderservice.entity.Order;
import marketplace.orderservice.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;


    public OrderController(OrderService orderService, WebClient.Builder builder) {
        this.orderService = orderService;
    }

    @PostMapping()
    public String createOrder(@RequestBody CreateOrderDto OrderDto, @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        ApiResponseDTO<Order> response = orderService.createOrder(OrderDto, userId);
        return "Order created successfully";
    }

}
