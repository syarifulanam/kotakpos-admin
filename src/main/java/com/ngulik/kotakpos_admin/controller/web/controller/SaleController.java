package com.ngulik.kotakpos_admin.controller.web.controller;

import com.ngulik.kotakpos_admin.controller.response.SaleResponse;
import com.ngulik.kotakpos_admin.enums.TransactionsStatus;
import com.ngulik.kotakpos_admin.service.RefundService;
import com.ngulik.kotakpos_admin.service.SaleService;
import com.ngulik.kotakpos_admin.util.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;
    private final RefundService refundService;

    @GetMapping
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String invoiceNumber,
            @RequestParam(required = false) TransactionsStatus status,
            Model model) {
        Pageable pageable = PageHelper.defaultPageable(sortDir, sortBy, page, size);
        Page<SaleResponse> sales = saleService.getSales(startDate, endDate, invoiceNumber, status, pageable);

        model.addAttribute("sales", sales);
        model.addAttribute("statuses", TransactionsStatus.values());
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("invoiceNumber", invoiceNumber);
        model.addAttribute("status", status);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        return "sales/index";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model) {
        try {
            SaleResponse saleResponse = saleService.getSaleById(id);
            model.addAttribute("sale", saleResponse);
            return "sales/view";
        } catch (RuntimeException e) {
            //Handle case where sale is not found
            return "redirect:/sales";
        }
    }

    @PostMapping("/refund/{id}")
    public String processRefund(@PathVariable Long id, @RequestParam String reason, RedirectAttributes redirectAttributes) {
        try {
            refundService.processFullRefund(id, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Sale has been successfully refunded.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing refund: " + e.getMessage());
        }
        return "redirect:/sales/view/" + id;
    }
}
