package marketplace.orderservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductDto(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        String category,
        UUID sellerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
