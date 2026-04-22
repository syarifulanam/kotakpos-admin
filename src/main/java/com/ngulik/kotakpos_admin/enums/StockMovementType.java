package com.ngulik.kotakpos_admin.enums;

import lombok.Getter;

@Getter
public enum StockMovementType {
    IN("In"),
    OUT("Out");

    private final String displayName;

    StockMovementType(String displayName) {
        this.displayName = displayName;
    }
}

