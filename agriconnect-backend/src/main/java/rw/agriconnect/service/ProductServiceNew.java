package rw.agriconnect.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import rw.agriconnect.model.Product;
import rw.agriconnect.model.User;

public interface ProductServiceNew {
    Product createProduct(Product product, MultipartFile image, User farmer);
    Product updateProduct(Long id, Product product, MultipartFile image, User farmer);
    void deleteProduct(Long id, User farmer);
    Product getProductById(Long id);
    Page<Product> getAllProducts(Pageable pageable);
    Page<Product> getProductsByFarmer(User farmer, Pageable pageable);
    Page<Product> searchProducts(
        String query,
        String category,
        Double minPrice,
        Double maxPrice,
        String location,
        Pageable pageable
    );
} 