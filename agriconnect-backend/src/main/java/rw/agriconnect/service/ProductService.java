package rw.agriconnect.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import rw.agriconnect.dto.ProductRequestDTO;
import rw.agriconnect.dto.ProductResponseDTO;
import rw.agriconnect.model.Product;
import rw.agriconnect.model.User;

import java.util.List;

public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO productDTO, User farmer);
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO productDTO, User farmer);
    void deleteProduct(Long id, User farmer);
    ProductResponseDTO getProductById(Long id);
    Page<ProductResponseDTO> getAllProducts(Pageable pageable);
    Page<ProductResponseDTO> getProductsByFarmer(User farmer, Pageable pageable);
    Page<ProductResponseDTO> searchProducts(String query, Pageable pageable);
    Product createProduct(Product product, MultipartFile image, User farmer);
    Product updateProduct(Long id, Product product, MultipartFile image, User farmer);
    List<Product> getAllProducts();
    List<Product> getProductsByFarmer(User farmer);
    List<Product> searchProducts(String query);
    
    // Paginated endpoints
    Page<Product> getAllProducts(Pageable pageable);
    Page<Product> getProductsByFarmer(User farmer, Pageable pageable);
    
    // Enhanced search with filters
    Page<Product> searchProducts(
        String query,
        String category,
        Double minPrice,
        Double maxPrice,
        String location,
        Pageable pageable
    );
} 