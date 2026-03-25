package com.ngulik.kotakpos_admin.controller.web.controller;

import com.ngulik.kotakpos_admin.dto.UserDto;
import com.ngulik.kotakpos_admin.enums.UserRole;
import com.ngulik.kotakpos_admin.enums.UserStatus;
import com.ngulik.kotakpos_admin.service.UserService;
import com.ngulik.kotakpos_admin.util.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.data.domain.Pageable;

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
}
