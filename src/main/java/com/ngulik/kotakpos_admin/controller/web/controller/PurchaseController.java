package com.ngulik.kotakpos_admin.controller.web.controller;

import com.ngulik.kotakpos_admin.entity.Purchase;
import com.ngulik.kotakpos_admin.entity.PurchaseItem;
import com.ngulik.kotakpos_admin.enums.PurchaseStatus;
import com.ngulik.kotakpos_admin.repository.ProductRepository;
import com.ngulik.kotakpos_admin.repository.SupplierRepository;
import com.ngulik.kotakpos_admin.service.PurchaseService;
import com.ngulik.kotakpos_admin.util.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    @GetMapping
    public String index(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) PurchaseStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model
    ) {
        Pageable pageable = PageHelper.defaultPageable(sortDir, sortBy, page, size);
        Page<Purchase> purchasePage = purchaseService.getAllPurchases(keyword, status, pageable);

        model.addAttribute("purchases", purchasePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("statuses", PurchaseStatus.values());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        return "purchases/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("purchase", purchaseService.preGenerateNewPurchase());
        model.addAttribute("suppliers", supplierRepository.findAll());
        return "purchases/create";
    }

    @PostMapping
    public String store(Purchase purchase) {
        Purchase newPurchase = purchaseService.createPurchase(purchase);
        return "redirect:/purchases/" + newPurchase.getId();
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("purchase", purchaseService.getPurchaseById(id));
        model.addAttribute("purchaseItem", new PurchaseItem());
        model.addAttribute("products", productRepository.findAll());
        return "purchases/show";
    }

    @PostMapping("/{id}/items")
    public String addItem(@PathVariable Long id, PurchaseItem purchaseItem) {
        purchaseService.addPurchaseItem(id, purchaseItem);
        return "redirect:/purchases/" +id;
    }

    @PostMapping("/{purchaseId}/items/{itemId}/delete")
    public String deleteItem(@PathVariable Long purchaseId, @PathVariable Long itemId) {
        purchaseService.deletePurchaseItem(purchaseId, itemId);
        return "redirect:/purchases/" + purchaseId;
    }

    @PostMapping("/{id}/submit")
    public String submit(@PathVariable Long id) {
        purchaseService.submitPurchase(id);
        return "redirect:/purchases/" + id;
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, @RequestParam String reason) {
        purchaseService.cancelPurchase(id, reason);
        return "redirect:/purchases/" + id;
    }

}
