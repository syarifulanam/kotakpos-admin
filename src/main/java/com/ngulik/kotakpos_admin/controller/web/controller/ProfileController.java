package com.ngulik.kotakpos_admin.controller.web.controller;

import com.ngulik.kotakpos_admin.dto.ChangePasswordDto;
import com.ngulik.kotakpos_admin.dto.UserProfileDto;
import com.ngulik.kotakpos_admin.enums.UserStatus;
import com.ngulik.kotakpos_admin.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        UserProfileDto profile = userService.getUserProfile(userDetails.getUsername());
        model.addAttribute("profile", profile);
        if (!model.containsAttribute("changePassword")) {
            model.addAttribute("changePassword", new ChangePasswordDto());
        }
        return "profile/index";
    }

    @PostMapping("/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @Valid @ModelAttribute("profile") UserProfileDto profileDto,
                                BindingResult result,
                                Model model, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            if (!model.containsAttribute("changePassword")) {
                model.addAttribute("changePassword", new ChangePasswordDto());
            }
            return "profile/index";
        }
        try {
            userService.updateUserProfile(userDetails.getUsername(), profileDto);
            redirectAttributes.addFlashAttribute("successMessage", "profile updated successfully.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
            if (!model.containsAttribute("changePassword")) {
                model.addAttribute("changePassword", new ChangePasswordDto());
            }
            return "profile/index";
        }
        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @Valid @ModelAttribute("changePassword") ChangePasswordDto changePasswordDto,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            UserProfileDto profile = userService.getUserProfile(userDetails.getUsername());
            model.addAttribute("profile", profile);
            return "profile/index";
        }

        try {
            userService.changePassword(userDetails.getUsername(), changePasswordDto);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully");
        } catch (IllegalArgumentException e) {
            UserProfileDto profile = userService.getUserProfile(userDetails.getUsername());
            model.addAttribute("profile", profile);
            model.addAttribute("errorMessage", e.getMessage());
            return "profile/index";
        } catch (Exception e) {
            UserProfileDto profile = userService.getUserProfile(userDetails.getUsername());
            model.addAttribute("profile", profile);
            model.addAttribute("errorMessage", "Failed to change password: " + e.getMessage());
            return "profile/index";
        }
        return "redirect:/profile";
    }
}
