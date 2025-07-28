package marketplace.orderservice.service;

import marketplace.orderservice.dto.ApiResponseDTO;
import marketplace.orderservice.dto.CreateOrderDto;
import marketplace.orderservice.dto.ProductDto;
import marketplace.orderservice.entity.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    ApiResponseDTO<Order> createOrder(CreateOrderDto order, UUID userId);

    ApiResponseDTO<Order> getOrderById(Long orderId);

    ApiResponseDTO<Order> updateOrder(Long orderId, Order orderDetails);

    ApiResponseDTO<List<Order>> getAllOrders();

    ApiResponseDTO<ProductDto> getProductById(UUID productId, String token);


}
