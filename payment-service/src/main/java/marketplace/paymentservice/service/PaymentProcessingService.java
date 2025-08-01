package marketplace.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marketplace.paymentservice.dto.OrderCreatedEventDto;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentProcessingService {

    private final ObjectMapper objectMapper;

    public void processOrderCreatedEvent(String orderEventJson) {
        try {
            OrderCreatedEventDto orderEvent = objectMapper.readValue(orderEventJson, OrderCreatedEventDto.class);

            log.info("Processing payment for order: {} with total amount: {}",
                    orderEvent.getOrderId(), orderEvent.getTotalAmount());

            // Here you would implement your payment processing logic
            processPayment(orderEvent);

        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize order event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process order event", e);
        }
    }

    private void processPayment(OrderCreatedEventDto orderEvent) {
        log.info("Payment processing initiated for order: {} with {} items",
                orderEvent.getOrderId(), orderEvent.getItems().size());

        // Log order details for demonstration
        orderEvent.getItems().forEach(item ->
            log.info("Item: {} - Quantity: {} - Price: {}",
                    item.getProductName(), item.getQuantity(), item.getTotalPrice())
        );
    }
}
