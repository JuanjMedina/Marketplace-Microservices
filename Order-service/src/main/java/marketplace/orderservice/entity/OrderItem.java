package marketplace.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String productName;

    private BigDecimal productPrice;

    private Integer quantity;

    private BigDecimal totalPrice;

    @ManyToOne()
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
