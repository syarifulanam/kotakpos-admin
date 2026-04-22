package com.ngulik.kotakpos_admin.controller.request;

import com.ngulik.kotakpos_admin.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequest {
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String paymentReference;
}
