package com.ngulik.kotakpos_admin.controller.web.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    // NOTE:
    // GlobalControlAdvice bertugas menyediakan data global untuk view / template
    // Kita tuh pakai requestURI di layout.html
    // tujuan nya untuk cek path URL misal '/dashboard', '/user', '/profil' dll
    // untuk menentukan mana menu yang active
    // ketika menu active, fontnya berwarna putih / dan jika lagi gak dibuka fontnya warna abu2

    @ModelAttribute("requestURI")
    public String requestURI(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
