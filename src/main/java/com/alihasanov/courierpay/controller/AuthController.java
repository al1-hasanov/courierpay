package com.alihasanov.courierpay.controller;

import com.alihasanov.courierpay.dto.AuthResponse;
import com.alihasanov.courierpay.service.AuthService;
import com.alihasanov.courierpay.dto.LoginRequest;
import com.alihasanov.courierpay.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
