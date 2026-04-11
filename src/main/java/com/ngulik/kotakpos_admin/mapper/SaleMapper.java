package com.ngulik.kotakpos_admin.mapper;

import com.ngulik.kotakpos_admin.controller.response.SaleItemResponse;
import com.ngulik.kotakpos_admin.controller.response.SaleResponse;
import com.ngulik.kotakpos_admin.entity.Payment;
import com.ngulik.kotakpos_admin.entity.Sale;
import com.ngulik.kotakpos_admin.entity.SaleItem;
import com.ngulik.kotakpos_admin.entity.SalesReturn;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SaleMapper {

    @Value("${app.base-url}")
    private String appBaseUrl;

    public SaleResponse toSaleResponse(Sale sale) {
        //NOTE: Handle payment info
        Payment payment = sale.getPayment();
        SaleResponse.PaymentInfo paymentInfo = Optional.ofNullable(payment)
                .map(p -> SaleResponse.PaymentInfo.builder()
                        .paymentMethod(p.getPaymentMethod().name())
                        .paymentReference(p.getPaymentReference())
                        .paidAt(p.getPaidAt())
                        .build())
                .orElse(null);

        //NOTE: Handle refund info
        List<SalesReturn> returns = Optional.ofNullable(sale.getReturns()).orElse(Collections.emptyList());
        List<SaleResponse.RefundInfo> refundInfos = returns.stream()
                .map(r -> SaleResponse.RefundInfo.builder()
                        .amount(r.getTotalRefundAmount())
                        .reason(r.getReason())
                        .refundedAt(r.getCreatedAt())
                        .refundedBy(r.getCreatedBy().getName())
                        .build())
                .collect(Collectors.toList());

        //NOTE: Determine derived status
        String derivedStatus = sale.getStatus().name();
        if (!returns.isEmpty()) {
            BigDecimal totalRefundAmount = returns.stream()
                    .map(SalesReturn::getTotalRefundAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (totalRefundAmount.compareTo(sale.getTotalAmount()) >= 0) {
                derivedStatus = "REFUNDED";
            } else {
                derivedStatus = "PARTIALLY_REFUNDED";
            }
        }

        return SaleResponse.builder()
                .id(sale.getId())
                .invoiceNumber(sale.getInvoiceNumber())
                .subtotal(sale.getSubtotal())
                .discountAmount(sale.getDiscountAmount())
                .dpp(sale.getDpp())
                .taxRate(sale.getTaxRate())
                .taxAmount(sale.getTotalAmount())
                .status(derivedStatus)
                .originalStatus(sale.getStatus().name())
                .note(sale.getNote())
                .createdAt(sale.getCreatedAt())
                .customer(SaleResponse.CustomerInfo.builder()
                        .id(sale.getCustomer().getId())
                        .name(sale.getCustomer().getName())
                        .build())
                .createdBy(SaleResponse.UserInfo.builder()
                        .id(sale.getCreatedBy().getId())
                        .name(sale.getCreatedBy().getName())
                        .build())
                .items(Optional.ofNullable(sale.getSaleItems()).orElse(Collections.emptyList()).stream()
                        .map(this::toSaleItemResponse)
                        .collect(Collectors.toList()))
                .payment(paymentInfo)
                .refunds(refundInfos)
                .build();
    }

    private SaleItemResponse toSaleItemResponse(SaleItem saleItem) {
        String imageUrl = appBaseUrl + saleItem.getProduct().getImageUrl();

        return SaleItemResponse.builder()
                .id(saleItem.getId())
                .productId(saleItem.getProduct().getId())
                .productName(saleItem.getProduct().getName())
                .quantity(saleItem.getQuantity())
                .imageUrl(imageUrl)
                .sellPrice(saleItem.getSellPrice())
                .subtotal(saleItem.getSubtotal())
                .build();
    }
}
