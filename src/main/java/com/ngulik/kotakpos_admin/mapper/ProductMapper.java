package com.ngulik.kotakpos_admin.mapper;

import com.ngulik.kotakpos_admin.dto.ProductDto;
import com.ngulik.kotakpos_admin.entity.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    @Value("${app.base.url}")
    private String appBaseUrl;

    public ProductDto toDto(Product product) {
        if (product == null) {
            return null;
        }

        String imageUrl = appBaseUrl + product.getImageUrl();

        ProductDto.ProductDtoBuilder builder = ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .sellPrice(product.getSellPrice())
                .costPrice(product.getCostPrice())
                .stock(product.getStock())
                .imageUrl(imageUrl)
                .barcode(product.getBarcode());

        if (product.getCategory() != null) {
            builder.categoryId(product.getCategory().getId());
        }

        return builder.build();
    }

    public ProductDto toProductDto(Product product) {
        if (product == null) {
            return null;
        }

        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setCategoryId(product.getCategory().getId());
        productDto.setCategory(product.getCategory());
        productDto.setUnit(product.getUnit());
        productDto.setSellPrice(product.getSellPrice());
        productDto.setCostPrice(product.getCostPrice());
        productDto.setStock(product.getStock());
        productDto.setImageUrl(product.getImageUrl());
        productDto.setBarcode(product.getBarcode());
        return productDto;
    }
}
