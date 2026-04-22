package com.ngulik.kotakpos_admin.controller.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class SaleRequest {
    private Long customerId;
    private String note;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal dpp;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private List<SaleItemRequest> items;
    private PaymentRequest payment;
}
