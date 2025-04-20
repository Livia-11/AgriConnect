package rw.agriconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.agriconnect.model.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
} 