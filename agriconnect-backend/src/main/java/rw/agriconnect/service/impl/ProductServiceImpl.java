package rw.agriconnect.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rw.agriconnect.dto.ProductRequestDTO;
import rw.agriconnect.dto.ProductResponseDTO;
import rw.agriconnect.exception.ResourceNotFoundException;
import rw.agriconnect.exception.UnauthorizedException;
import rw.agriconnect.model.Product;
import rw.agriconnect.model.User;
import rw.agriconnect.repository.ProductRepository;
import rw.agriconnect.service.ProductService;

@Service
@CacheConfig(cacheNames = {"products", "farmerProducts", "productSearch"})
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO productDTO, User farmer) {
        Product product = new Product();
        mapProductRequestDTOToProduct(productDTO, product);
        product.setFarmer(farmer);
        Product savedProduct = productRepository.save(product);
        return ProductResponseDTO.fromProduct(savedProduct);
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productDTO, User farmer) {
        Product existingProduct = getProductEntityById(id);
        if (!existingProduct.getFarmer().equals(farmer)) {
            throw new UnauthorizedException("You are not authorized to update this product");
        }
        mapProductRequestDTOToProduct(productDTO, existingProduct);
        Product updatedProduct = productRepository.save(existingProduct);
        return ProductResponseDTO.fromProduct(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id, User farmer) {
        Product product = getProductEntityById(id);
        if (!product.getFarmer().equals(farmer)) {
            throw new UnauthorizedException("You are not authorized to delete this product");
        }
        productRepository.delete(product);
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        Product product = getProductEntityById(id);
        return ProductResponseDTO.fromProduct(product);
    }

    @Override
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductResponseDTO::fromProduct);
    }

    @Override
    public Page<ProductResponseDTO> getProductsByFarmer(User farmer, Pageable pageable) {
        return productRepository.findByFarmer(farmer, pageable)
                .map(ProductResponseDTO::fromProduct);
    }

    @Override
    public Page<ProductResponseDTO> searchProducts(String query, Pageable pageable) {
        return productRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, pageable)
                .map(ProductResponseDTO::fromProduct);
    }

    private Product getProductEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private void mapProductRequestDTOToProduct(ProductRequestDTO dto, Product product) {
        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setCategory(dto.getCategory());
    }
} 