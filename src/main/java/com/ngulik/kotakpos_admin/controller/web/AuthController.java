package com.ngulik.kotakpos_admin.controller.web;

import com.ngulik.kotakpos_admin.dto.LoginDto;
import com.ngulik.kotakpos_admin.dto.UserDto;
import com.ngulik.kotakpos_admin.enums.UserRole;
import com.ngulik.kotakpos_admin.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collection;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final UserService userService;

    @GetMapping("/login")
    public String login(Model model) {
        if (!model.containsAttribute("loginDto")) {
            model.addAttribute("loginDto", new LoginDto());
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute("loginDto") LoginDto loginDto,
                            BindingResult result,
                            HttpServletRequest request,
                            HttpServletResponse response,
                            Model model) {

        if (result.hasErrors()) {
            return "login";
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );

            // CHECK ROLE
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            boolean isAuthorized = authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_" + UserRole.ADMIN.name()) ||
                            a.getAuthority().equals("ROLE_" + UserRole.STAFF.name()) ||
                            a.getAuthority().equals("ROLE_" + UserRole.OWNER.name()));

            if (!isAuthorized) {
                model.addAttribute("error", "Access denied. Only Admin, Staff, and Owner can login here.");
                return "login";
            }

            // NOTE: simpan userContext untuk kita gunakan nanti buat dapetin user.id misal untuk keperluan CreatedBy
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);
            return "redirect:/dashboard";
        } catch (DisabledException e) {
            model.addAttribute("error", "Wrong Email or password");
            return "login";

        } catch (LockedException e) {
            model.addAttribute("error", "Locked Account");
            return "login";

        } catch (AuthenticationException e) {
            model.addAttribute("error", "Failed to login: " + e.getMessage());
            return "login";
        }
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userDto") UserDto userDto,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        try {
            userService.registerUser(userDto);
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }

        return "redirect:/login?registered";
    }
}