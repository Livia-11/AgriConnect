package rw.agriconnect.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.agriconnect.model.Notification;
import rw.agriconnect.model.User;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUser(User user, Pageable pageable);
    
    Page<Notification> findByUserAndReadFalse(User user, Pageable pageable);
    
    List<Notification> findTop10ByUserAndReadFalseOrderByCreatedAtDesc(User user);
    
    long countByUserAndReadFalse(User user);
}