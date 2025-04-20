package rw.agriconnect.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.agriconnect.dto.ProductRequestDTO;
import rw.agriconnect.dto.ProductResponseDTO;
import rw.agriconnect.model.User;

public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO productDTO, User farmer);
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO productDTO, User farmer);
    void deleteProduct(Long id, User farmer);
    ProductResponseDTO getProductById(Long id);
    Page<ProductResponseDTO> getAllProducts(Pageable pageable);
    Page<ProductResponseDTO> getProductsByFarmer(User farmer, Pageable pageable);
    Page<ProductResponseDTO> searchProducts(String query, Pageable pageable);
} 