package com.ngulik.kotakpos_admin.controller.web.controller;

import com.ngulik.kotakpos_admin.dto.ProductDto;
import com.ngulik.kotakpos_admin.entity.Adjustment;
import com.ngulik.kotakpos_admin.entity.Category;
import com.ngulik.kotakpos_admin.entity.Product;
import com.ngulik.kotakpos_admin.entity.StockMovement;
import com.ngulik.kotakpos_admin.enums.AdjustmentType;
import com.ngulik.kotakpos_admin.enums.ProductUnit;
import com.ngulik.kotakpos_admin.exception.error.ResourceNotFoundException;
import com.ngulik.kotakpos_admin.repository.CategoryRepository;
import com.ngulik.kotakpos_admin.repository.ProductRepository;
import com.ngulik.kotakpos_admin.repository.StockMovementRepository;
import com.ngulik.kotakpos_admin.service.AdjustmentService;
import com.ngulik.kotakpos_admin.service.ProductService;
import com.ngulik.kotakpos_admin.util.PageHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final AdjustmentService adjustmentService;

    @GetMapping
    public String index(Model model,
                        @RequestParam(required = false) String query,
                        @RequestParam(required = false) Long categoryId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "id") String sortBy,
                        @RequestParam(defaultValue = "desc") String sortDir) {
        Pageable pageable = PageHelper.defaultPageable(sortDir, sortBy, page, size);
        Page<Product> productPage = productService.getAllProducts(query, categoryId, pageable);
        List<Category> categories = categoryRepository.findAllByOrderByIdDesc();

        model.addAttribute("productPage", productPage);
        model.addAttribute("categories", categories);
        model.addAttribute("query", query);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        return "products/index";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        ProductDto productDto = new ProductDto();
        productDto.setStock(0L); // set default stock to 0

        List<Category> categories = categoryRepository.findAllByOrderByIdDesc();

        model.addAttribute("productDto", productDto);
        model.addAttribute("categories", categories);
        model.addAttribute("units", ProductUnit.values()); // Kirim enum value
        return "products/form";
    }

    @PostMapping
    public String createProduct(@Valid @ModelAttribute("productDto") ProductDto productDto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            List<Category> categories = categoryRepository.findAllByOrderByIdDesc();
            model.addAttribute("categories", categories);
            return "products/form";
        }
        try {
            productService.createProduct(productDto);
            redirectAttributes.addFlashAttribute("successMessage", "Product created successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating product: " + e.getMessage());
            return "redirect:/products/new";
        }
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProductDto productDto = productService.getProductById(id);
        if (productDto == null) {
            return "redirect:/products";
        }

        model.addAttribute("productDto", productDto);
        List<Category> categories = categoryRepository.findAllByOrderByIdDesc();
        model.addAttribute("categories", categories);
        model.addAttribute("units", ProductUnit.values()); // Kirim enum value

        return "products/form";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("productDto") ProductDto productDto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            List<Category> categories = categoryRepository.findAllByOrderByIdDesc();
            model.addAttribute("categories", categories);
            return "products/form";
        }
        try {
            productService.updateProduct(id, productDto);
            redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully.");
        } catch (IOException | RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating product: " + e.getMessage());
            return "redirect:/products/edit/" + id;
        }

        return "redirect:/products";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting product.");
        }
        return "redirect:/products";
    }

    @GetMapping("/view/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        ProductDto productDto = productService.getProductById(id);
        if (productDto == null) {
            return "redirect:/products";
        }

        model.addAttribute("productDto", productDto);
        return "products/view";
    }

    @GetMapping("/adjust/{productId}")
    public String showStockMovements(@PathVariable Long productId, Model model) {
        Optional<Product> productOptional = productService.findProductById(productId);
        if (productOptional.isEmpty()) {
            return "redirect:/products";
        }

        Product product = productOptional.get();
        List<StockMovement> stockMovements = stockMovementRepository.findByProductId(productId);
        model.addAttribute("product", product);
        model.addAttribute("stockMovements", stockMovements);
        return "products/adjustments/index";
    }

    @GetMapping("/adjust/new/{productId}")
    public String showAdjustmentForm(@PathVariable Long productId, Model model) {
        Optional<Product> productOptional = productService.findProductById(productId);
        if (productOptional.isEmpty()) {
            return "redirect:/products";
        }

        Product product = productOptional.get();
        Adjustment adjustment = new Adjustment();
        adjustment.setProduct(product);
        model.addAttribute("adjustment", adjustment);
        model.addAttribute("adjustmentTypes", Arrays.asList(AdjustmentType.values()));
        return "products/adjustments/form";
    }

    @PostMapping("/adjustments")
    public String processAdjustment(@ModelAttribute Adjustment adjustment, RedirectAttributes redirectAttributes) {
        // Because the form only submits the product ID, we need to fetch the full product object here,
        Product product = productRepository.findById(adjustment.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid product Id:" + adjustment.getProduct().getId()));
        adjustment.setProduct(product);

        try {
            adjustmentService.createdAdjustment(adjustment);
            redirectAttributes.addFlashAttribute("successMessage", "Stock adjusted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adjusting stock: " + e.getMessage());
        }
        return "redirect:/products/adjust/" + product.getId();
    }
}
