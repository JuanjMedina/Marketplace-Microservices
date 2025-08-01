package marketplace.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
public class KafkaTopicConfiguration {

    @Bean
    public NewTopic generateTopic () {

        Map<String , String> configs = new HashMap<>();

        configs.put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE); // Set cleanup policy to delete
        configs.put(TopicConfig.RETENTION_MS_CONFIG, TimeUnit.DAYS.toMillis(7) + ""); // Retain messages for 7 days
        configs.put(TopicConfig.SEGMENT_BYTES_CONFIG, "1073741824"); // Set segment size to 1GB)
        configs.put(TopicConfig.MAX_MESSAGE_BYTES_CONFIG, "10485760"); // Set max message size to 10MB


        return TopicBuilder.name(KafkaTopics.ORDER_CREATED)  // ← Usando la constante
                .partitions(2)
                .replicas(2)
                .configs(configs)
                .build();
    }
}
