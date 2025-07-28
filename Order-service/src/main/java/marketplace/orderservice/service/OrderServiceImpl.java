package marketplace.orderservice.service;

import lombok.extern.slf4j.Slf4j;
import marketplace.orderservice.dto.ApiResponseDTO;
import marketplace.orderservice.dto.CreateOrderDto;
import marketplace.orderservice.dto.OrderItemDto;
import marketplace.orderservice.dto.OrderResponseDto;
import marketplace.orderservice.dto.ProductDto;
import marketplace.orderservice.entity.Order;
import marketplace.orderservice.entity.OrderItem;
import marketplace.orderservice.entity.OrderStaus;
import marketplace.orderservice.exception.AuthenticationException;
import marketplace.orderservice.exception.OrderException;
import marketplace.orderservice.exception.ProductNotFoundException;
import marketplace.orderservice.mapper.OrderMapper;
import marketplace.orderservice.repository.OrderRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webclient;
    private final OrderMapper orderMapper;

    private String extractToken() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getTokenValue();
        }
        return null;
    }

    public OrderServiceImpl(OrderRepository orderRepository, WebClient.Builder webclient, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.webclient = webclient
                .baseUrl("http://localhost:8081/api/products")
                .build();
        this.orderMapper = orderMapper;
    }

    @Override
    public ApiResponseDTO<ProductDto> getProductById(UUID productId, String token) {
        log.debug("Attempting to get product with ID: {}", productId);

        if (productId == null) {
            throw new ProductNotFoundException("null");
        }

        if (token == null || token.trim().isEmpty()) {
            throw new AuthenticationException("Authentication token is required to fetch product information");
        }

        try {
            ApiResponseDTO<ProductDto> response = webclient
                    .get()
                    .uri("/{productId}", productId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponseDTO<ProductDto>>() {})
                    .timeout(Duration.ofSeconds(10)) // Timeout configuration
                    .block();

            if (response == null || !response.success()) {
                throw new ProductNotFoundException(productId.toString());
            }

            return response;

        } catch (WebClientResponseException.NotFound e) {
            log.error("Product not found with ID: {}", productId);
            throw new ProductNotFoundException(productId.toString());
        } catch (WebClientResponseException e) {
            log.error("Product service returned error for productId: {}, status: {}, body: {}",
                     productId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new OrderException("Product service error: " + e.getStatusCode());
        } catch (WebClientException e) {
            log.error("Network error calling product service for productId: {}, error: {}", productId, e.getMessage());
            throw new OrderException("Unable to connect to product service");
        } catch (Exception e) {
            log.error("Unexpected error calling product service for productId: {}, error: {}", productId, e.getMessage());
            throw new OrderException("Unexpected error retrieving product information");
        }
    }

    @Override
    @Transactional
    public ApiResponseDTO<Order> createOrder(CreateOrderDto orderDto, UUID userId) {
        log.info("Creating order for user: {}", userId);

        String token = extractToken();
        if (token == null || token.trim().isEmpty()) {
            throw new AuthenticationException("Authentication token is missing or invalid");
        }

        try {
            Order newOrder = Order.builder()
                    .buyerId(userId)
                    .status(OrderStaus.PENDING)
                    .build();

            BigDecimal total = processOrderItems(orderDto.items(), newOrder, token);

            if (total.compareTo(BigDecimal.ZERO) <= 0) {
                throw new OrderException("Order total must be greater than zero");
            }

            newOrder.setTotalAmount(total);
            Order savedOrder = orderRepository.save(newOrder);

            log.info("Order created successfully with ID: {} for user: {}", savedOrder.getId(), userId);

            return ApiResponseDTO.<Order>builder()
                    .success(true)
                    .message("Order created successfully")
                    .data(savedOrder)
                    .build();

        } catch (Exception e) {
            log.error("Error creating order for user: {}, error: {}", userId, e.getMessage());
            throw new OrderException("Failed to create order: " + e.getMessage());
        }
    }


    private BigDecimal processOrderItems(List<OrderItemDto> items, Order order, String token) {
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemDto item : items) {
            try {
                ApiResponseDTO<ProductDto> productResponse = getProductById(item.productId(), token);
                ProductDto product = productResponse.data();

                if (product.price() == null || product.price().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new OrderException("Invalid product price for product: " + product.name());
                }

                BigDecimal subtotal = product.price().multiply(BigDecimal.valueOf(item.quantity()));

                OrderItem orderItem = OrderItem.builder()
                        .productName(product.name())
                        .productPrice(product.price())
                        .quantity(item.quantity())
                        .totalPrice(subtotal)
                        .order(order)
                        .build();

                order.addItem(orderItem);
                total = total.add(subtotal);

                log.debug("Added item to order - Product: {}, Price: {}, Quantity: {}, Subtotal: {}",
                         product.name(), product.price(), item.quantity(), subtotal);

            } catch (ProductNotFoundException e) {
                log.error("Product not found while processing order: {}", item.productId());
                throw new OrderException("Product with ID " + item.productId() + " is not available");
            } catch (Exception e) {
                log.error("Error processing item {}: {}", item.productId(), e.getMessage());
                throw new OrderException("Failed to process product: " + item.productId());
            }
        }

        return total;
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
