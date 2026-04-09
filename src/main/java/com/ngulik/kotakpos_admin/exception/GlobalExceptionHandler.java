package com.ngulik.kotakpos_admin.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.ngulik.kotakpos_admin.controller.response.ApiErrorResponse;
import com.ngulik.kotakpos_admin.exception.error.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.ngulik.kotakpos_admin.exception.error.BadRequestException;

import jakarta.servlet.http.HttpServletRequest;
import javassist.NotFoundException;

@Slf4j
@ControllerAdvice
@Order(1)
public class GlobalExceptionHandler {

    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api");
    }

    //NOTE: Digunakan untuk kasus ketika resource tidak ditemukan secara logika bisnis/ custom error not found
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleNotFoundException(NotFoundException exception, HttpServletRequest request, Model model) {
        log.error("ex.getMessage(): {}", exception.getMessage());
        log.error("ex.getLocalizedMessage(): {}", exception.getLocalizedMessage());
        if (isApiRequest(request)) {
            ApiErrorResponse response = ApiErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .message(exception.getMessage())
                    .path(request.getRequestURI())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            model.addAttribute("errorCode", HttpStatus.NOT_FOUND.value());
            model.addAttribute("errorTitle", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("errorMessage", "Sorry, this page doesn't exist");
            return "error/error";
        }
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleNoResourceFoundException(NoResourceFoundException exception, HttpServletRequest request, Model model) {
        log.error("ex.getMessage(): {}", exception.getMessage());
        log.error("ex.getLocalizedMessage(): {}", exception.getLocalizedMessage());
        if (isApiRequest(request)) {
            ApiErrorResponse response = ApiErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .message(exception.getMessage())
                    .path(request.getRequestURI())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            model.addAttribute("errorCode", HttpStatus.NOT_FOUND.value());
            model.addAttribute("errorTitle", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("errorMessage", "The page you're looking for can't be found.");
            return "error/error";
        }
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleResourceNotFoundException(ResourceNotFoundException exception, HttpServletRequest request,
                                                  Model model) {
        log.error("ex.getMessage(): {}", exception.getMessage());
        log.error("ex.getLocalizedMessage(): {}", exception.getLocalizedMessage());
        if (isApiRequest(request)) {
            ApiErrorResponse response = ApiErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .message(exception.getMessage())
                    .path(request.getRequestURI())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            model.addAttribute("errorCode", HttpStatus.NOT_FOUND.value());
            model.addAttribute("errorTitle", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("errorMessage", exception.getMessage());
            return "error/error";
        }
    }

    @ExceptionHandler(Exception.class)
    public Object handleGeneralException(Exception exception, HttpServletRequest request, Model model) {
        log.error("ex.getMessage(): {}", exception.getMessage());
        log.error("ex.getLocalizedMessage(): {}", exception.getLocalizedMessage());
        if (isApiRequest(request)) {
            ApiErrorResponse response = ApiErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .message(exception.getMessage())
                    .path(request.getRequestURI())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            model.addAttribute("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            model.addAttribute("errorTitle", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            model.addAttribute("errorMessage", exception.getMessage());
            return "error/error";
        }
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDenied(AccessDeniedException exception, HttpServletRequest request, Model model) {
        log.error("ex.getMessage(): {}", exception.getMessage());
        log.error("ex.getLocalizedMessage(): {}", exception.getLocalizedMessage());
        if (isApiRequest(request)) {
            ApiErrorResponse response = ApiErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.FORBIDDEN.value())
                    .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                    .message("Access denied")
                    .path(request.getRequestURI())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        } else {
            model.addAttribute("errorCode", HttpStatus.FORBIDDEN.value());
            model.addAttribute("errorTitle", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("errorMessage", exception.getMessage());
            return "error/error";
        }
    }

    @ExceptionHandler(LockedException.class)
    public Object handleLockedException(LockedException exception, HttpServletRequest request, Model model) {
        log.error("ex.getMessage(): {}", exception.getMessage());
        log.error("ex.getLocalizedMessage(): {}", exception.getLocalizedMessage());
        if (isApiRequest(request)) {
            ApiErrorResponse response = ApiErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .message("Account is locked")
                    .path(request.getRequestURI())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } else {
            model.addAttribute("errorCode", HttpStatus.UNAUTHORIZED.value());
            model.addAttribute("errorTitle", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("errorMessage", exception.getMessage());
            return "error/error";
        }
    }

    @ExceptionHandler(DisabledException.class)
    public Object handleDisabledException(DisabledException exception, HttpServletRequest request, Model model) {
        log.error("ex.getMessage(): {}", exception.getMessage());
        log.error("ex.getLocalizedMessage(): {}", exception.getLocalizedMessage());
        if (isApiRequest(request)) {
            ApiErrorResponse response = ApiErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .message("Account is disabled")
                    .path(request.getRequestURI())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } else {
            model.addAttribute("errorCode", HttpStatus.UNAUTHORIZED.value());
            model.addAttribute("errorTitle", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("errorMessage", exception.getMessage());
            return "error/error";
        }
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public Object handleAuthenticationException(Exception exception, HttpServletRequest request, Model model) {
        log.error("ex.getMessage(): {}", exception.getMessage());
        log.error("ex.getLocalizedMessage(): {}", exception.getLocalizedMessage());
        if (isApiRequest(request)) {
            ApiErrorResponse response = ApiErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .message("invalid email or password")
                    .path(request.getRequestURI())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } else {
            model.addAttribute("errorCode", HttpStatus.UNAUTHORIZED.value());
            model.addAttribute("errorTitle", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("errorMessage", exception.getMessage());
            return "error/error";
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request,
                                            Model model) {
        log.error("ex.getMessage(): {}", exception.getMessage());
        log.error("ex.getLocalizedMessage(): {}", exception.getLocalizedMessage());
        if (isApiRequest(request)) {
            List<ApiErrorResponse.ValidationError> validationErrors = exception.getBindingResult().getFieldErrors().stream()
                    .map(error -> new ApiErrorResponse.ValidationError(error.getField(), error.getDefaultMessage()))
                    .collect(Collectors.toList());

            ApiErrorResponse response = ApiErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .message("Validation failed")
                    .path(request.getRequestURI())
                    .validationErrors(validationErrors)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else {
            model.addAttribute("errorCode", HttpStatus.BAD_REQUEST.value());
            model.addAttribute("errorTitle", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("errorMessage", exception.getMessage());
            return "error/error";
        }
    }

    @ExceptionHandler({BadRequestException.class})
    public Object handleBadRequestException(BadRequestException exception, HttpServletRequest request, Model model) {
        log.error("ex.getMessage(): {}", exception.getMessage());
        log.error("ex.getLocalizedMessage(): {}", exception.getLocalizedMessage());
        if (isApiRequest(request)) {
            ApiErrorResponse response = ApiErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .message(exception.getMessage())
                    .path(request.getRequestURI())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else {
            model.addAttribute("errorCode", HttpStatus.BAD_REQUEST.value());
            model.addAttribute("errorTitle", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("errorMessage", exception.getMessage());
            return "error/error";
        }
    }
}
