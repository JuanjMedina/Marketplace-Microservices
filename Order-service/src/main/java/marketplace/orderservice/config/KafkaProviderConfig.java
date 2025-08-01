package marketplace.orderservice.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProviderConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers = "localhost:9092";

    public Map<String, Object> producerConfig(){
        Map<String , Object > properties = new HashMap<>();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Add reliability configurations
        properties.put(ProducerConfig.ACKS_CONFIG, "all"); // Wait for all replicas to acknowledge
        properties.put(ProducerConfig.RETRIES_CONFIG, 3); // Retry on failure
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // Prevent duplicate messages
        properties.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000); // 2 minutes timeout
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000); // 30 seconds request timeout

        return properties;
    }

    @Bean
    public ProducerFactory <String , String > providerFactory(){
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String , String > kafkaTemplate(ProducerFactory<String , String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
