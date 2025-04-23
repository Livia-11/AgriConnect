package rw.agriconnect.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rw.agriconnect.dto.PaginatedResponse;
import rw.agriconnect.dto.ProductRequestDTO;
import rw.agriconnect.dto.ProductResponseDTO;
import rw.agriconnect.model.Product;
import rw.agriconnect.model.User;
import rw.agriconnect.service.ProductService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Management", description = "APIs for managing agricultural products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasRole('FARMER')")
    @Operation(summary = "Create a new product", description = "Creates a new product. Only accessible by authenticated farmers.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    @CacheEvict(value = "products", allEntries = true)
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestPart("product") ProductRequestDTO productRequest,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal User farmer) {
        Product product = productRequest.toProduct();
        Product createdProduct = productService.createProduct(product, image, farmer);
        return ResponseEntity.status(HttpStatus.CREATED)
                           .body(ProductResponseDTO.fromProduct(createdProduct));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Updates an existing product. Only accessible by the product owner.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Unauthorized access"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @CacheEvict(value = "products", allEntries = true)
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestPart("product") ProductRequestDTO productRequest,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal User farmer) {
        Product product = productRequest.toProduct();
        Product updatedProduct = productService.updateProduct(id, product, image, farmer);
        return ResponseEntity.ok(ProductResponseDTO.fromProduct(updatedProduct));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Deletes a product. Only accessible by the product owner.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Unauthorized access"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @CacheEvict(value = "products", allEntries = true)
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal User farmer) {
        productService.deleteProduct(id, farmer);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieves a paginated list of all products.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    @Cacheable(value = "products", key = "#page + '-' + #size")
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Product> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(createPagedResponse(products.map(ProductResponseDTO::fromProduct)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves a specific product by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved product"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @Cacheable(value = "products", key = "#id")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(ProductResponseDTO.fromProduct(product));
    }

    @GetMapping("/farmer")
    @Operation(summary = "Get products by farmer", description = "Retrieves all products for a specific farmer.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products"),
        @ApiResponse(responseCode = "404", description = "Farmer not found")
    })
    @Cacheable(value = "farmerProducts", key = "#farmer.id + '-' + #page + '-' + #size")
    public ResponseEntity<Map<String, Object>> getProductsByFarmer(
            @AuthenticationPrincipal User farmer,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Product> products = productService.getProductsByFarmer(farmer, pageable);
        return ResponseEntity.ok(createPagedResponse(products.map(ProductResponseDTO::fromProduct)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Searches products by title or description.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    @Cacheable(value = "productSearch", key = "#query + '-' + #page + '-' + #size")
    public ResponseEntity<Map<String, Object>> searchProducts(
            @Parameter(description = "Search query") @RequestParam(required = false) String query,
            @Parameter(description = "Search category") @RequestParam(required = false) String category,
            @Parameter(description = "Minimum price") @RequestParam(required = false) Double minPrice,
            @Parameter(description = "Maximum price") @RequestParam(required = false) Double maxPrice,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Product> products = productService.searchProducts(
            query, category, minPrice, maxPrice, pageable
        );
        return ResponseEntity.ok(createPagedResponse(products.map(ProductResponseDTO::fromProduct)));
    }

    private Map<String, Object> createPagedResponse(Page<ProductResponseDTO> page) {
        Map<String, Object> response = new HashMap<>();
        response.put("content", page.getContent());
        response.put("currentPage", page.getNumber());
        response.put("totalItems", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("pageSize", page.getSize());
        return response;
    }
} 