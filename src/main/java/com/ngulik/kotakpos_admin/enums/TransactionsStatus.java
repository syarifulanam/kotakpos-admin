package com.ngulik.kotakpos_admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionsStatus {
    PENDING("PENDING"),
    COMPLETED("COMPLETED"),
    REFUNDED("REFUNDED"),
    FAILED("FAILED");

    private final String displayName;
}
