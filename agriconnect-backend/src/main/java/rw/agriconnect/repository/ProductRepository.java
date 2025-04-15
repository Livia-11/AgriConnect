package rw.agriconnect.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rw.agriconnect.model.Product;
import rw.agriconnect.model.User;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByFarmer(User farmer, Pageable pageable);
    Page<Product> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title, String description, Pageable pageable);
} 