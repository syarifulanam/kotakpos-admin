package com.ngulik.kotakpos_admin.exception;

import com.ngulik.kotakpos_admin.exception.error.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import javassist.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Order(1)
public class GlobalExceptionHandler {

    // NOTE: Digunakan untuk kasus ketika resource tidak ditemukan secara logika bisnis/ custom error not found
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleNotFoundException(NotFoundException exception, HttpServletRequest request, Model model) {
        model.addAttribute("errorCode", HttpStatus.NOT_FOUND.value());
        model.addAttribute("errorTitle", HttpStatus.NOT_FOUND.getReasonPhrase());
        model.addAttribute("errorMessage", "Sorry, this page doesn't exist.");
        return "error/error";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleNoResourceFoundException(NoResourceFoundException exception, HttpServletRequest request, Model model) {
        model.addAttribute("errorCode", HttpStatus.NOT_FOUND.value());
        model.addAttribute("errorTitle", HttpStatus.NOT_FOUND.getReasonPhrase());
        model.addAttribute("errorMessage", "The page you're looking for can't be found.");
        return "error/error";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleResourceNotFoundException(ResourceNotFoundException exception, HttpServletRequest request,
                                                  Model model) {
        model.addAttribute("errorCode", HttpStatus.NOT_FOUND.value());
        model.addAttribute("errorTitle", HttpStatus.NOT_FOUND.getReasonPhrase());
        model.addAttribute("errorMessage", exception.getMessage());
        return "error/error";
    }
}
