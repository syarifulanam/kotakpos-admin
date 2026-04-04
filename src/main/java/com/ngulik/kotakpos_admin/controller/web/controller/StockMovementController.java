package com.ngulik.kotakpos_admin.controller.web.controller;

import com.ngulik.kotakpos_admin.entity.StockMovement;
import com.ngulik.kotakpos_admin.enums.ReferenceType;
import com.ngulik.kotakpos_admin.enums.StockMovementType;
import com.ngulik.kotakpos_admin.service.StockMovementService;
import com.ngulik.kotakpos_admin.util.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@RequestMapping("/stock-movements")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @GetMapping
    public String listStockMovements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) StockMovementType type,
            @RequestParam(required = false) ReferenceType referenceType,
            @RequestParam(required = false) Long referenceId,
            @RequestParam(required = false) Long productId,
            Model model) {
        Pageable pageable = PageHelper.defaultPageable(sortDir, sortBy, page, size);
        Page<StockMovement> stockMovements =
                stockMovementService.getStockMovements(type, referenceType, referenceId, productId, pageable);
        model.addAttribute("stockMovements", stockMovements);
        model.addAttribute("stockMovementType", StockMovementType.values());
        model.addAttribute("referenceType", ReferenceType.values());
        model.addAttribute("type", type);
        model.addAttribute("referenceType", referenceType);
        model.addAttribute("productId", productId);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        return "pages/stock_movement/index";
    }
}
