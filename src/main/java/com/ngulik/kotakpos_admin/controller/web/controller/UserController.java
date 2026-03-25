package com.ngulik.kotakpos_admin.controller.web.controller;

import com.ngulik.kotakpos_admin.dto.UserDto;
import com.ngulik.kotakpos_admin.enums.UserRole;
import com.ngulik.kotakpos_admin.enums.UserStatus;
import com.ngulik.kotakpos_admin.service.UserService;
import com.ngulik.kotakpos_admin.util.PageHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String index(Model model,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(required = false) String name,
                        @RequestParam(required = false) String email,
                        @RequestParam(required = false) UserRole role,
                        @RequestParam(required = false) UserStatus status,
                        @RequestParam(defaultValue = "id") String sortBy,
                        @RequestParam(defaultValue = "desc") String sortDir) {
        Pageable pageable = PageHelper.defaultPageable(sortDir, sortBy, page, size);
        Page<UserDto> users = userService.getAllUsers(name, email, role, status, pageable);

        model.addAttribute("users", users);
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("role", role);
        model.addAttribute("status", status);
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("statuses", UserStatus.values());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        return "users/index";
    }

    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new UserDto());
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("statuses", UserStatus.values());
        return "users/form";
    }

    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("user") UserDto userDto, BindingResult result, Model model,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roles", UserRole.values());
            model.addAttribute("statuses", UserStatus.values());
            return "users/form";
        }
        boolean isNew = userDto.getId() == null;
        userService.saveUser(userDto);
        redirectAttributes.addFlashAttribute("successMessage",
                isNew ? "User ccreated successfully!" : "user updated successfully!");
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String edituserForm(@PathVariable Long id, Model model) {
        UserDto user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("statuses", UserStatus.values());
        return "users/form";
    }
}
