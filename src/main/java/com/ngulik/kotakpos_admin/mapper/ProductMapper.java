package com.ngulik.kotakpos_admin.mapper;

import com.ngulik.kotakpos_admin.dto.ProductDto;
import com.ngulik.kotakpos_admin.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

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
