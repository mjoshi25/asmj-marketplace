package com.asmj.marketplace.auth.controller;

import com.asmj.marketplace.auth.dto.AuthDtos.*;
import com.asmj.marketplace.auth.service.AuthService;
import com.asmj.marketplace.common.response.ApiResponse;
import com.asmj.marketplace.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {
    private final AuthService service;
    @PostMapping("/register") public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest r){ return ApiResponse.ok("Registration successful",service.register(r)); }
    @PostMapping("/login") public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest r){ return ApiResponse.ok("Login successful",service.login(r)); }
    @GetMapping("/me") public ApiResponse<User> me(Authentication a){ User u=service.current(a.getName());u.setPassword(null);return ApiResponse.ok("Current user",u); }
}
