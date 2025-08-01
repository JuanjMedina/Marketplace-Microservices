package marketplace.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marketplace.orderservice.config.KafkaTopics;
import marketplace.orderservice.dto.OrderCreatedEventDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendOrderCreatedEvent(OrderCreatedEventDto event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            String key = event.orderId().toString();

            log.info("Publishing order created event for order: {}", event.orderId());

            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(KafkaTopics.ORDER_CREATED, key, eventJson);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully published order created event for order: {} to topic: {} with offset: {}",
                            event.orderId(), KafkaTopics.ORDER_CREATED, result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish order created event for order: {} to topic: {}. Error: {}",
                            event.orderId(), KafkaTopics.ORDER_CREATED, ex.getMessage(), ex);
                }
            });

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize order created event for order: {}. Error: {}",
                     event.orderId(), e.getMessage(), e);
            throw new RuntimeException("Failed to serialize order event", e);
        } catch (Exception e) {
            log.error("Unexpected error publishing order created event for order: {}. Error: {}",
                     event.orderId(), e.getMessage(), e);
            throw new RuntimeException("Failed to publish order event", e);
        }
    }
}
