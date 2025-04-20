package rw.agriconnect.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rw.agriconnect.model.Product;
import rw.agriconnect.model.User;
import rw.agriconnect.repository.ProductRepository;
import rw.agriconnect.service.FileStorageService;
import rw.agriconnect.service.ProductService;

import java.util.NoSuchElementException;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, FileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public Product createProduct(Product product, MultipartFile image, User farmer) {
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.storeFile(image);
            product.setImageUrl(imageUrl);
        }
        product.setFarmer(farmer);
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product product, MultipartFile image, User farmer) {
        Product existingProduct = getProductById(id);
        
        if (!existingProduct.getFarmer().getId().equals(farmer.getId())) {
            throw new SecurityException("You are not authorized to update this product");
        }

        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.storeFile(image);
            product.setImageUrl(imageUrl);
        } else {
            product.setImageUrl(existingProduct.getImageUrl());
        }

        product.setId(id);
        product.setFarmer(farmer);
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id, User farmer) {
        Product product = getProductById(id);
        
        if (!product.getFarmer().getId().equals(farmer.getId())) {
            throw new SecurityException("You are not authorized to delete this product");
        }

        if (product.getImageUrl() != null) {
            fileStorageService.deleteFile(product.getImageUrl());
        }
        
        productRepository.delete(product);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> getProductsByFarmer(User farmer, Pageable pageable) {
        return productRepository.findByFarmer(farmer, pageable);
    }

    @Override
    public Page<Product> searchProducts(
            String query,
            String category,
            Double minPrice,
            Double maxPrice,
            String location,
            Pageable pageable
    ) {
        return productRepository.searchProducts(query, category, minPrice, maxPrice, location, pageable);
    }
} 