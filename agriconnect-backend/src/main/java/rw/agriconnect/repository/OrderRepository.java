package rw.agriconnect.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.agriconnect.model.Order;
import rw.agriconnect.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    Page<Order> findByBuyer(User buyer, Pageable pageable);
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);
    List<Order> findTop5ByBuyerOrderByCreatedAtDesc(User buyer);
} 