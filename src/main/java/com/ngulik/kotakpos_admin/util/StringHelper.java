package com.ngulik.kotakpos_admin.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StringHelper {

    public static String generateInvoiceNumber(long latestId) {
        long nextId = latestId + 1;
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String numberPart = String.format("%09d", nextId);

        return "INV-" + datePart + numberPart;
    }
}
