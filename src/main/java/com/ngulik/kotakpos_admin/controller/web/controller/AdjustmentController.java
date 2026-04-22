package com.ngulik.kotakpos_admin.controller.web.controller;

import com.ngulik.kotakpos_admin.entity.Adjustment;
import com.ngulik.kotakpos_admin.enums.AdjustmentType;
import com.ngulik.kotakpos_admin.service.AdjustmentService;
import com.ngulik.kotakpos_admin.util.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/adjustments")
public class AdjustmentController {

    private final AdjustmentService adjustmentService;

    @GetMapping
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) AdjustmentType type,
            @RequestParam(required = false) Long productId,
            Model model) {
        Pageable pageable = PageHelper.defaultPageable(sortDir, sortBy, page, size);
        Page<Adjustment> adjustments = adjustmentService.getAdjustments(type, productId, pageable);

        model.addAttribute("adjustments", adjustments);
        model.addAttribute("adjustmentTypes", AdjustmentType.values());
        model.addAttribute("type", type);
        model.addAttribute("productId", productId);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        return "adjustments/index";
    }
}
