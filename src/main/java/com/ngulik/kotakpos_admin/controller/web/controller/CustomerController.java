package com.ngulik.kotakpos_admin.controller.web.controller;

import com.ngulik.kotakpos_admin.entity.Customer;
import com.ngulik.kotakpos_admin.exception.error.ResourceNotFoundException;
import com.ngulik.kotakpos_admin.repository.CustomerRepository;
import com.ngulik.kotakpos_admin.util.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository customerRepository;

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
        Page<Customer> customers = customerRepository.search(name, phone, email, pageable);

        model.addAttribute("customers", customers);
        model.addAttribute("name", name);
        model.addAttribute("phone", phone);
        model.addAttribute("email", email);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        return "customers/index";
    }

    @GetMapping("/new")
    public String newCustomer(Model model) {
        model.addAttribute("customer", new Customer());
        return "customers/form";
    }

    @PostMapping
    public String createCustomer(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes) {
        customerRepository.save(customer);
        redirectAttributes.addFlashAttribute("successMessage", "Customer created successfully");
        return "redirect:/customers";
    }

    @GetMapping("/edit/{id}")
    public String editCustomer(@PathVariable Long id, Model model) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Id:" + id));
        model.addAttribute("customer", customer);
        return "customers/form";
    }

    @PostMapping("/update/{id}")
    public String updateCustomer(@PathVariable Long id, @ModelAttribute Customer customer,
                                 RedirectAttributes redirectAttributes) {
        customer.setId(id);
        customerRepository.save(customer);

        redirectAttributes.addFlashAttribute("successMessage", "Customer updated successfully");
        return "redirect:/customers";
    }

    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        customerRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Customer deleted successfully");
        return "redirect:/customers";
    }
}
