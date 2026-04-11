package com.ngulik.kotakpos_admin.controller.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class SaleItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Long quantity;
    private String imageUrl;
    private BigDecimal sellPrice;
    private BigDecimal subtotal;
}
