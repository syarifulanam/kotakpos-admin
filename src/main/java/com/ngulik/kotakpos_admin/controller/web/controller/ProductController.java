package com.ngulik.kotakpos_admin.controller.web.controller;

import com.ngulik.kotakpos_admin.dto.ProductDto;
import com.ngulik.kotakpos_admin.entity.Category;
import com.ngulik.kotakpos_admin.entity.Product;
import com.ngulik.kotakpos_admin.enums.ProductUnit;
import com.ngulik.kotakpos_admin.repository.CategoryRepository;
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
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

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
        return "products/create";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute("productDto") ProductDto productDto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            List<Category> categories = categoryRepository.findAllByOrderByIdDesc();
            model.addAttribute("categories", categories);
            model.addAttribute("units", ProductUnit.values());
            return "products/create";
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
        Optional<Product> productOptional = productService.getProductById(id);
        if (productOptional.isEmpty()) {
            return "redirect:/products";
        }

        Product product = productOptional.get();
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setCategoryId(product.getCategory().getId());
        productDto.setUnit(product.getUnit());
        productDto.setSellPrice(product.getSellPrice());
        productDto.setCostPrice(product.getCostPrice());
        productDto.setStock(product.getStock());

        model.addAttribute("productDto", productDto);
        List<Category> categories = categoryRepository.findAllByOrderByIdDesc();
        model.addAttribute("categories", categories);
        model.addAttribute("product", product);
        model.addAttribute("units", ProductUnit.values()); // Kirim enum value

        return "products/edit";
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
//            model.addAttribute("units", ProductUnit.values());
            return "products/edit";
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
        Optional<Product> productOptional = productService.getProductById(id);
        if (productOptional.isEmpty()) {
            return "redirect:/products";
        }

        Product product = productOptional.get();
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setCategory(product.getCategory());
        productDto.setSellPrice(product.getSellPrice());
        productDto.setCostPrice(product.getCostPrice());
        productDto.setStock(product.getStock());
        productDto.setImageUrl(product.getImageUrl());
        productDto.setUnit(product.getUnit());
        productDto.setBarcode(product.getBarcode());

        model.addAttribute("productDto", productDto);
        model.addAttribute("product", product);

        return "products/view";
    }
}
