package marketplace.paymentservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marketplace.paymentservice.service.PaymentProcessingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerListener {

    private final PaymentProcessingService paymentProcessingService;

    @KafkaListener(topics = "order-generated", groupId = "payment-service-group")
    public void handleOrderCreatedEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Received message from topic: {}, partition: {}, offset: {}", topic, partition, offset);

        try {
            paymentProcessingService.processOrderCreatedEvent(message);
            log.info("Successfully processed order event from Kafka");
        } catch (Exception e) {
            log.error("Failed to process order event: {}", e.getMessage(), e);
            // Here you could implement retry logic or send to dead letter queue
        }
    }
}
