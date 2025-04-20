package rw.agriconnect.service;

import rw.agriconnect.dto.CategoryRequestDTO;
import rw.agriconnect.dto.CategoryResponseDTO;
import rw.agriconnect.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryResponseDTO createCategory(CategoryRequestDTO request);
    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO request);
    void deleteCategory(Long id);
    CategoryResponseDTO getCategoryById(Long id);
    List<CategoryResponseDTO> getAllCategories();
    Category getCategoryEntity(Long id);
} 