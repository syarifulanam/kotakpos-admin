package com.ngulik.kotakpos_admin.util;

import java.math.BigDecimal;

public class PriceCalculatorHelper {

    public static BigDecimal calculateSubtotal(BigDecimal costPrice, Long quantity) {
        return costPrice.multiply(new BigDecimal(quantity));
    }
}
