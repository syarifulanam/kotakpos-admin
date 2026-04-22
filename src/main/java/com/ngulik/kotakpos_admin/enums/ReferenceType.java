package com.ngulik.kotakpos_admin.enums;

import lombok.Getter;

@Getter
public enum ReferenceType {
    PURCHASE("purchase"),
    SALE("Sale"),
    ADJUSTMENT("Adjustment"),
    SALES_RETURN("Sales Return");

    private final String displayName;

    ReferenceType(String displayName) {
        this.displayName = displayName;
    }
}
