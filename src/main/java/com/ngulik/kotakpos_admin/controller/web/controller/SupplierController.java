package com.ngulik.kotakpos_admin.controller.web.controller;

import com.ngulik.kotakpos_admin.entity.Supplier;
import com.ngulik.kotakpos_admin.exception.error.ResourceNotFoundException;
import com.ngulik.kotakpos_admin.repository.SupplierRepository;
import com.ngulik.kotakpos_admin.util.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierRepository supplierRepository;

    @GetMapping
    public String index(Model model,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(required = false) String name,
                        @RequestParam(required = false) String phone,
                        @RequestParam(required = false) String email,
                        @RequestParam(defaultValue = "id") String sortBy,
                        @RequestParam(defaultValue = "desc") String sortDir) {
        Pageable pageable = PageHelper.defaultPageable(sortDir, sortBy, page, size);
        Page<Supplier> suppliers = supplierRepository.search(name, phone, email, pageable);

        model.addAttribute("suppliers", suppliers);
        model.addAttribute("name", name);
        model.addAttribute("phone", phone);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        return "suppliers/index";
    }

    @GetMapping("/new")
    public String newSupplier(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "suppliers/form";
    }

    @PostMapping
    public String createSupplier(@ModelAttribute Supplier supplier, RedirectAttributes redirectAttributes) {
        supplierRepository.save(supplier);
        redirectAttributes.addFlashAttribute("successMessage", "Supplier created successfull!");
        return "redirect:/suppliers";
    }

    @GetMapping("/edit/{id}")
    public String editSupplier(@PathVariable Long id, Model model) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Id:" + id));
        model.addAttribute("supplier", supplier);
        return "suppliers/form";
    }

    @PostMapping("/update/{id}")
    public String updateSupplier(@PathVariable Long id, @ModelAttribute Supplier supplier,
                                 RedirectAttributes redirectAttributes) {
        supplier.setId(id);
        supplierRepository.save(supplier);
        redirectAttributes.addFlashAttribute("successMessage", "Supplier updated successfully!");
        return "redirect:/suppliers";
    }

    @PostMapping("/delete/{id}")
    public String deleteSupplier(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        supplierRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Supplier deleted successfully!");
        return "redirect:/suppliers";
    }
}
