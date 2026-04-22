package com.ngulik.kotakpos_admin.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringHelperTest {

    @Test
    void generateInvoiceNumber() {
        String invoiceNumber = StringHelper.generateInvoiceNumber(0);
        System.out.println("invoiceNumber: " + invoiceNumber);
    }
}