package com.ngulik.kotakpos_admin.controller.api.controller;

import com.ngulik.kotakpos_admin.controller.response.ApiPageResponse;
import com.ngulik.kotakpos_admin.controller.response.ApiResponse;
import com.ngulik.kotakpos_admin.dto.CategoryDto;
import com.ngulik.kotakpos_admin.entity.Category;
import com.ngulik.kotakpos_admin.mapper.categoryMapper;
import com.ngulik.kotakpos_admin.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Pageable;
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class ApiCategoryController {

    private final CategoryService categoryService;
    private final categoryMapper categoryMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<ApiPageResponse<CategoryDto>>> getCategories(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 10, sort = "id")Pageable pageable) {
        Page<Category> categoriesPage = categoryService.getAllCategories(query, pageable);
        Page<CategoryDto> categoriesDtoPage = categoriesPage.map(categoryMapper::toDto);
        ApiPageResponse<CategoryDto> apiPageResponse = ApiPageResponse.from(categoriesDtoPage);
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", apiPageResponse));
    }
}
