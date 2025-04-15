package rw.agriconnect.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.agriconnect.model.Product;
import rw.agriconnect.model.User;

public interface ProductService {
    Product createProduct(Product product, User farmer);
    Product updateProduct(Long id, Product product, User farmer);
    void deleteProduct(Long id, User farmer);
    Product getProductById(Long id);
    Page<Product> getAllProducts(Pageable pageable);
    Page<Product> getProductsByFarmer(User farmer, Pageable pageable);
    Page<Product> searchProducts(String query, Pageable pageable);
} 