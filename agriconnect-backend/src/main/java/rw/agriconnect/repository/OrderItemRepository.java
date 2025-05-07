package rw.agriconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rw.agriconnect.model.Order;
import rw.agriconnect.model.OrderItem;
import rw.agriconnect.model.User;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
    
    List<OrderItem> findByFarmer(User farmer);
    
    @Query("SELECT oi FROM OrderItem oi JOIN oi.order o WHERE o.status = :status AND oi.farmer = :farmer")
    List<OrderItem> findByOrderStatusAndFarmer(Order.OrderStatus status, User farmer);
} 