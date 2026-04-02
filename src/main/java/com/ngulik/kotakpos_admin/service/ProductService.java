package com.ngulik.kotakpos_admin.service;

import com.ngulik.kotakpos_admin.dto.ProductDto;
import com.ngulik.kotakpos_admin.entity.Category;
import com.ngulik.kotakpos_admin.entity.Product;
import com.ngulik.kotakpos_admin.exception.error.ResourceNotFoundException;
import com.ngulik.kotakpos_admin.repository.CategoryRepository;
import com.ngulik.kotakpos_admin.repository.ProductRepository;
import com.ngulik.kotakpos_admin.util.ProductHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.allowed-extensions}")
    private List<String> allowedExtensions;

    @Value("${file.max-size}")
    private long maxFileSize;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Page<Product> getAllProducts(String query, Long categoryId, Pageable pageable) {
        return productRepository.search(query, categoryId, pageable);
    }

    public Product createProduct(ProductDto productDto) throws IOException {
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = new Product();
        product.setName(productDto.getName());
        product.setCategory(category);
        product.setUnit(productDto.getUnit());
        product.setSellPrice(productDto.getSellPrice());
        product.setCostPrice(productDto.getCostPrice());
        product.setStock(productDto.getStock());

        Product savedProduct = productRepository.save(product);

        String barcode = ProductHelper.generateBarcode(savedProduct);
        savedProduct.setBarcode(barcode);

        if (productDto.getImage() != null && !productDto.getImage().isEmpty()) {
            String imageUrl = ProductHelper.saveImage(productDto.getImage(), savedProduct,uploadDir, allowedExtensions, maxFileSize);
            savedProduct.setImageUrl(imageUrl);
        }

        return productRepository.save(savedProduct);
    }
}
