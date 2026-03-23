package com.ngulik.kotakpos_admin.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    //    @Order(1)
    @Bean
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults()) //NOTE : nanti kebutuhan nya untuk di API
                .authorizeHttpRequests((request) -> request
                        .requestMatchers("/register", "/login", "/css/**", "/js/**", "/images/**", "/uploads/**",
                                "/access-denied", "/static/**", "/favicon.ico", "/swagger-ui/**", "/v3/api-docs", "/swagger-ui.html").permitAll()

                        .requestMatchers("/reports/**").hasAnyRole("ADMIN", "OWNER")

                        .requestMatchers("/users/**").hasRole("ADMIN")

                        .requestMatchers("/customers/**", "/suppliers/**", "/category/**", "/products/**",
                                "/purchases/**", "/stock-movements/**", "/adjustments/**", "/sales/**")
                        .hasAnyRole("ADMIN", "STAFF")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login-processing")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .exceptionHandling((exceptions) -> exceptions
                        .accessDeniedPage("/access-denied")
                );
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    //NOTE: authenticationManager untuk bisa melakukan autentikasi manual di web controller / API
    // dia akan cek username dan passwor
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // NOTE: PasswordEndcoder dengan algoritma BCrypt
    // berfungsi untuk mengenkripsi / hashing password kita
    // sebelum disimpan ke database dan memverifikasi password saat login
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //NOTE: inisiasi / persiapan agar kita bisa menyimpan SecurityContext
    // setelah user login untuk keperluan createdBy misalnya kita akan tau siapa yg login
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );
    }
}