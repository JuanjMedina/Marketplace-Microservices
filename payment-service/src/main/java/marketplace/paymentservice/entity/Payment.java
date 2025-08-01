package marketplace.paymentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    UUID orderId;

    BigDecimal amount;

    @Enumerated(EnumType.STRING)
    PaymentStatus status;

    LocalDateTime paidAt;

}
