package com.ngulik.kotakpos_admin.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class SaleResponse {
    private Long id;
    private String invoiceNumber;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal dpp;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String status;
    private String note;
    private LocalDateTime createdAt;

    private CustomerInfo customer;
    private UserInfo createdBy;
    private List<SaleItemResponse> items;
    private PaymentInfo payment;
    private List<RefundInfo> refunds;

    @JsonIgnore
    private String originalStatus;

    @Getter
    @Builder
    public static class CustomerInfo {
        private Long id;
        private String name;
    }

    @Getter
    @Builder
    public static class UserInfo {
        private Long id;
        private String name;
    }

    @Getter
    @Builder
    public static class PaymentInfo {
        private String paymentMethod;
        private String paymentReference;
        private LocalDateTime paidAt;
    }

    @Getter
    @Builder
    public static class RefundInfo {
        private BigDecimal amount;
        private String reason;
        private LocalDateTime refundedAt;
        private String refundedBy;
    }
}
