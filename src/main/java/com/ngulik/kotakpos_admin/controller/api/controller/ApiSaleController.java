package com.ngulik.kotakpos_admin.controller.api.controller;

import com.ngulik.kotakpos_admin.controller.request.RefundRequest;
import com.ngulik.kotakpos_admin.controller.request.SaleRequest;
import com.ngulik.kotakpos_admin.controller.response.ApiPageResponse;
import com.ngulik.kotakpos_admin.controller.response.ApiResponse;
import com.ngulik.kotakpos_admin.controller.response.SaleResponse;
import com.ngulik.kotakpos_admin.enums.TransactionsStatus;
import com.ngulik.kotakpos_admin.service.RefundService;
import com.ngulik.kotakpos_admin.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sales")
public class ApiSaleController {

    private final SaleService saleService;
    private final RefundService refundService;

//**
// Get a paginated list of sales, with optional filtering by date range, invoice number, and status. Example URL:
//    /api/sales?invoiceNumber=INV-2024-05-0001&status=COMPLETED
    @GetMapping
    public ResponseEntity<ApiResponse<ApiPageResponse<SaleResponse>>> getSales(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String invoiceNumber,
            @RequestParam(required = false)TransactionsStatus status) {

        Page<SaleResponse> salePage = saleService.getSales(startDate, endDate, invoiceNumber, status, pageable);
        ApiPageResponse<SaleResponse> apiPageResponse = ApiPageResponse.from(salePage);
        return ResponseEntity.ok(ApiResponse.success("Sales retrieved successfully", apiPageResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SaleResponse>> getSaleById(@PathVariable Long id) {
        SaleResponse saleResponse = saleService.getSaleById(id);
        return ResponseEntity.ok(ApiResponse.success("Sale retrieved successfully", saleResponse));
    }

    //Create
    @PostMapping
    public ResponseEntity<ApiResponse<SaleResponse>> createSale(@RequestBody SaleRequest saleRequest) {
        SaleResponse createdSale = saleService.createSale(saleRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Sale created successfully", createdSale));
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<ApiResponse<SaleResponse>> processRefund(
            @PathVariable Long id,
            @Valid @RequestBody RefundRequest refundRequest) {
        refundService.processFullRefund(id, refundRequest.getReason());
        SaleResponse updatedSale = saleService.getSaleById(id); // Fetch the updated sale details
        return ResponseEntity.ok(ApiResponse.success("Sale has been successfully refunded.", updatedSale));
    }
}
