package com.ngulik.kotakpos_admin.controller.api.controller;

import com.ngulik.kotakpos_admin.controller.request.LoginRequest;
import com.ngulik.kotakpos_admin.controller.request.RegisterRequest;
import com.ngulik.kotakpos_admin.controller.response.ApiResponse;
import com.ngulik.kotakpos_admin.controller.response.LoginResponse;
import com.ngulik.kotakpos_admin.controller.response.RegisterResponse;
import com.ngulik.kotakpos_admin.dto.UserDto;
import com.ngulik.kotakpos_admin.entity.User;
import com.ngulik.kotakpos_admin.enums.UserRole;
import com.ngulik.kotakpos_admin.exception.error.BadRequestException;
import com.ngulik.kotakpos_admin.security.JwtUtils;
import com.ngulik.kotakpos_admin.service.TokenBlacklistService;
import com.ngulik.kotakpos_admin.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ApiAuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final TokenBlacklistService tokenBlacklistService;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserDto userDto = new UserDto();
        userDto.setName(request.getName());
        userDto.setEmail(request.getEmail());
        userDto.setPassword(request.getPassword());

        User registeredUser = userService.registerUser(userDto);

        RegisterResponse responseData = RegisterResponse.builder()
                .id(registeredUser.getId())
                .name(registeredUser.getName())
                .email(registeredUser.getEmail())
                .role(registeredUser.getRole())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", responseData));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Check role
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAuthorized = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + UserRole.CASHIER.name()));

        if (!isAuthorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<LoginResponse>builder()
                            .status("error")
                            .message("Access denied. Only Cashier can login here.")
                            .build());
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtils.generateToken(userDetails);

        LoginResponse responseData = LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration)
                .build();

        return ResponseEntity.ok(ApiResponse.success("Login successful", responseData));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        Date expirationDate = jwtUtils.extractExpiration(token);
        long expirationMillis = expirationDate.getTime() - System.currentTimeMillis();

        log.info("Logout token with expirationMillis: {}", expirationMillis);

        if (expirationMillis > 0) {
            tokenBlacklistService.blacklistToken(token, expirationMillis);
            return ResponseEntity.ok(ApiResponse.success("Logout successful", "OK"));
        }

        throw new BadRequestException("Token already expired");
    }
}