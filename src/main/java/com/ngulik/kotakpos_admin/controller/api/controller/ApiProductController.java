package com.ngulik.kotakpos_admin.controller.api.controller;

import com.ngulik.kotakpos_admin.controller.response.ApiPageResponse;
import com.ngulik.kotakpos_admin.controller.response.ApiResponse;
import com.ngulik.kotakpos_admin.dto.ProductDto;
import com.ngulik.kotakpos_admin.entity.Product;
import com.ngulik.kotakpos_admin.mapper.ProductMapper;
import com.ngulik.kotakpos_admin.service.ProductService;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ApiProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<ApiPageResponse<ProductDto>>> getProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 10, sort = "id")Pageable pageable) {
        Page<Product> productsPage = productService.getAllProducts(query, categoryId, pageable);

        Page<ProductDto> productsDtoPage = productsPage.map(productMapper::toDto);

        ApiPageResponse<ProductDto> apiPageResponse = ApiPageResponse.from(productsDtoPage);
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", apiPageResponse));
    }
}
