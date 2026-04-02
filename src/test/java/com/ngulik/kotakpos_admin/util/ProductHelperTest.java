package com.ngulik.kotakpos_admin.util;

import com.ngulik.kotakpos_admin.entity.Category;
import com.ngulik.kotakpos_admin.entity.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductHelperTest {

    @Test
    void generateBarcode() {

       String barcode = ProductHelper.generateBarcode(Product.builder()
                .id(10L)
                .category(Category.builder()
                        .id(12L)
                        .build())
                .build());

        System.out.println("barcode: " + barcode);
    }
}