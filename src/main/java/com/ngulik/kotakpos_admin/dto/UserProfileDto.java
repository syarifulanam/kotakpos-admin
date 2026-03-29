package com.ngulik.kotakpos_admin.dto;

import com.ngulik.kotakpos_admin.enums.UserRole;
import com.ngulik.kotakpos_admin.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserProfileDto {

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    private UserRole role;

    private UserStatus status;

    private LocalDateTime joinedSince;
}
