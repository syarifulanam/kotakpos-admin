package com.ngulik.kotakpos_admin.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ngulik.kotakpos_admin.entity.Category;
import com.ngulik.kotakpos_admin.enums.ProductUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Unit is required")
    private ProductUnit unit;

    @NotNull(message = "Sell price is required")
    private BigDecimal sellPrice;

    @NotNull(message = "Cost price is required")
    private BigDecimal costPrice;

    private Long stock;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MultipartFile image;

    private String barcode;

    private String imageUrl;

    private Category category;
}
