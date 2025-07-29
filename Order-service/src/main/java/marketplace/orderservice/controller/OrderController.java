package marketplace.orderservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Order Management", description = "APIs for managing orders in the marketplace")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @PostMapping()
    @Operation(
        summary = "Create a new order",
        description = "Creates a new order for the authenticated user with the specified items. The system will validate product availability and calculate the total amount."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Order created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDTO.class),
                examples = @ExampleObject(
                    name = "Successful order creation",
                    value = """
                    {
                        "success": true,
                        "message": "Order created successfully",
                        "data": {
                            "id": "123e4567-e89b-12d3-a456-426614174000",
                            "buyerId": "456e7890-e89b-12d3-a456-426614174001",
                            "status": "PENDING",
                            "totalAmount": 299.98,
                            "createdAt": "2024-01-15T10:30:00",
                            "updatedAt": "2024-01-15T10:30:00",
                            "items": [
                                {
                                    "productName": "Laptop",
                                    "productPrice": 299.99,
                                    "quantity": 1,
                                    "totalPrice": 299.99
                                }
                            ]
                        }
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid order data provided",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation error",
                    value = """
                    {
                        "success": false,
                        "message": "Validation failed",
                        "data": null
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Authentication error",
                    value = """
                    {
                        "success": false,
                        "message": "Authentication token is missing or invalid",
                        "data": null
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Product not found",
                    value = """
                    {
                        "success": false,
                        "message": "Product with ID 123e4567-e89b-12d3-a456-426614174000 is not available",
                        "data": null
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponseDTO<OrderResponseDto>> createOrder(
            @Parameter(
                description = "Order creation data containing list of items to order",
                required = true,
                content = @Content(
                    examples = @ExampleObject(
                        name = "Order creation request",
                        value = """
                        {
                            "items": [
                                {
                                    "productId": "123e4567-e89b-12d3-a456-426614174000",
                                    "quantity": 2
                                },
                                {
                                    "productId": "456e7890-e89b-12d3-a456-426614174001",
                                    "quantity": 1
                                }
                            ]
                        }
                        """
                    )
                )
            )
            @Valid @RequestBody CreateOrderDto orderDto,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
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
    @Operation(
        summary = "Get my orders",
        description = "Retrieves all orders for the authenticated user. Accessible by buyers and admins."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Orders retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDTO.class),
                examples = @ExampleObject(
                    name = "User orders list",
                    value = """
                    {
                        "success": true,
                        "message": "Orders retrieved successfully",
                        "data": [
                            {
                                "id": "123e4567-e89b-12d3-a456-426614174000",
                                "buyerId": "456e7890-e89b-12d3-a456-426614174001",
                                "status": "PENDING",
                                "totalAmount": 299.98,
                                "createdAt": "2024-01-15T10:30:00",
                                "updatedAt": "2024-01-15T10:30:00",
                                "items": []
                            }
                        ]
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Insufficient permissions - requires buyer or admin role"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No orders found for user",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "No orders found",
                    value = """
                    {
                        "success": false,
                        "message": "No orders found for user: 456e7890-e89b-12d3-a456-426614174001",
                        "data": null
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponseDTO<List<Order>>> getMyOrders(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        ApiResponseDTO<List<Order>> response = orderService.getMyOrders(userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('admin_client_role')")
    @GetMapping("/admin/all")
    @Operation(
        summary = "Get all orders (Admin only)",
        description = "Retrieves all orders in the system. This endpoint is restricted to administrators only."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "All orders retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDTO.class),
                examples = @ExampleObject(
                    name = "All orders list",
                    value = """
                    {
                        "success": true,
                        "message": "Orders retrieved successfully",
                        "data": [
                            {
                                "id": "123e4567-e89b-12d3-a456-426614174000",
                                "buyerId": "456e7890-e89b-12d3-a456-426614174001",
                                "status": "PENDING",
                                "totalAmount": 299.98,
                                "createdAt": "2024-01-15T10:30:00",
                                "updatedAt": "2024-01-15T10:30:00",
                                "items": []
                            }
                        ]
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Insufficient permissions - admin role required"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No orders found in the system"
        )
    })
    public ResponseEntity<?> getAllOrders() {
        ApiResponseDTO<List<Order>> response = orderService.getAllOrders();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('admin_client_role') or hasRole('buyer_client_role')")
    @GetMapping("/{orderId}")
    @Operation(
        summary = "Get order by ID",
        description = "Retrieves a specific order by its ID. Users can only access their own orders, while admins can access any order."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseDTO.class),
                examples = @ExampleObject(
                    name = "Order details",
                    value = """
                    {
                        "success": true,
                        "message": "Order retrieved successfully",
                        "data": {
                            "id": "123e4567-e89b-12d3-a456-426614174000",
                            "buyerId": "456e7890-e89b-12d3-a456-426614174001",
                            "status": "PENDING",
                            "totalAmount": 299.98,
                            "createdAt": "2024-01-15T10:30:00",
                            "updatedAt": "2024-01-15T10:30:00",
                            "items": [
                                {
                                    "productName": "Laptop",
                                    "productPrice": 299.99,
                                    "quantity": 1,
                                    "totalPrice": 299.99
                                }
                            ]
                        }
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Insufficient permissions - buyer or admin role required"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Order not found or access denied",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Order not found",
                    value = """
                    {
                        "success": false,
                        "message": "Order not found for user: 456e7890-e89b-12d3-a456-426614174001 with order ID: 123e4567-e89b-12d3-a456-426614174000",
                        "data": null
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponseDTO<Order>> getOrderById(
            @Parameter(
                description = "The unique identifier of the order to retrieve",
                required = true,
                example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable UUID orderId,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        ApiResponseDTO<Order> response = orderService.getOrderById(orderId, userId);
        return ResponseEntity.ok(response);
    }

}
