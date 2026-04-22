package com.ngulik.kotakpos_admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdjustmentType {
    ADJUSTMENT_IN("Adjustment In"),
    ADJUSTMENT_OUT("Adjustment Out");

    private final String displayName;

}
