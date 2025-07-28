package marketplace.orderservice.service;

import lombok.extern.slf4j.Slf4j;
import marketplace.orderservice.dto.ApiResponseDTO;
import marketplace.orderservice.dto.CreateOrderDto;
import marketplace.orderservice.dto.OrderItemDto;
import marketplace.orderservice.dto.ProductDto;
import marketplace.orderservice.entity.Order;
import marketplace.orderservice.entity.OrderItem;
import marketplace.orderservice.entity.OrderStaus;
import marketplace.orderservice.repository.OrderRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webclient;

    private String extractToken() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getTokenValue();
        }
        return null;
    }

    public OrderServiceImpl(OrderRepository orderRepository, WebClient.Builder webclient) {
        this.orderRepository = orderRepository;
        this.webclient = webclient
                .baseUrl("http://localhost:8081/api/products")
                .build();
    }

    @Override
    public ApiResponseDTO<ProductDto> getProductById(UUID productId, String token) {
        log.debug("Attempting to get product with ID: {}", productId);

        if (productId == null) {
            log.error("Product ID is null");
            return ApiResponseDTO.<ProductDto>builder()
                    .success(false)
                    .message("Product ID cannot be null")
                    .build();
        }

        try {
            return webclient
                    .get().uri("/{productId}", productId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO<ProductDto>>() {
                    })
                    .block();
        } catch (Exception e) {
            log.error("Error calling product service for productId: {}, error: {}", productId, e.getMessage());
            return ApiResponseDTO.<ProductDto>builder()
                    .success(false)
                    .message("Failed to retrieve product: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponseDTO<Order> createOrder(CreateOrderDto order, UUID userId) {
        Order newOrder = Order.builder()
                .buyerId(userId)
                .status(OrderStaus.PENDING)
                .build();

        List<OrderItemDto> items = order.items();

        String token = extractToken();

        if (token == null || token.isEmpty()) {
            return ApiResponseDTO.<Order>builder()
                    .success(false)
                    .message("Authentication token is missing")
                    .build();
        }
        log.debug("token" + token);


        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemDto item : items) {
            ApiResponseDTO<ProductDto> response = getProductById(item.productId(), token);

            if (response != null && response.success()) {

                ProductDto product = response.data();

                BigDecimal subtotal = product.price().multiply(BigDecimal.valueOf(item.quantity()));

                OrderItem newOrderItem = OrderItem.builder()
                        .productName(product.name())
                        .productPrice(product.price())
                        .quantity(item.quantity())
                        .totalPrice(subtotal)
                        .order(newOrder)
                        .build();

                newOrder.addItem(newOrderItem);
                total = total.add(subtotal);
            }

        }

        newOrder.setTotalAmount(total);
        orderRepository.save(newOrder);

        return ApiResponseDTO.<Order>builder()
                .success(true)
                .message("Order created successfully")
                .data(newOrder)
                .build();
    }

    @Override
    public ApiResponseDTO<Order> getOrderById(Long orderId) {
        return null;
    }

    @Override
    public ApiResponseDTO<Order> updateOrder(Long orderId, Order orderDetails) {
        return null;
    }

    @Override
    public ApiResponseDTO<List<Order>> getAllOrders() {
        return null;
    }


}
