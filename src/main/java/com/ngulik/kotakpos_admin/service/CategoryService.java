package com.ngulik.kotakpos_admin.service;

import com.ngulik.kotakpos_admin.dto.CategoryDto;
import com.ngulik.kotakpos_admin.entity.Category;
import com.ngulik.kotakpos_admin.exception.error.ResourceNotFoundException;
import com.ngulik.kotakpos_admin.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<Category> getAllCategories(String query, Pageable pageable) {
        return categoryRepository.search(query, pageable);
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    @Transactional
    public Category createCategory(CategoryDto categoryDto) {
        Category category = Category.builder()
                .name(categoryDto.getName())
                .icon(categoryDto.getIcon())
                .description(categoryDto.getDescription())
                .build();
        return categoryRepository.save(category);
    }

    @Transactional
    public  Category updateCategory(Long id, CategoryDto categoryDto) {
        Category category = getCategoryById(id);

        category.setName(categoryDto.getName());
        category.setIcon(categoryDto.getIcon());
        category.setDescription(categoryDto.getDescription());

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}
