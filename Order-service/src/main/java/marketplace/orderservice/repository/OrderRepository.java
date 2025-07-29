package marketplace.orderservice.repository;

import marketplace.orderservice.entity.Order;
import marketplace.orderservice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByBuyerId(UUID buyerId);

    Optional<Order> findByIdAndBuyerId(UUID id, UUID buyerId);

}
