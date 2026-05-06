package com.alihasanov.courierpay.service;

import com.alihasanov.courierpay.dto.AuthResponse;
import com.alihasanov.courierpay.dto.LoginRequest;
import com.alihasanov.courierpay.dto.RegisterRequest;
import com.alihasanov.courierpay.entity.AppUser;
import com.alihasanov.courierpay.enums.UserStatus;
import com.alihasanov.courierpay.exception.BusinessException;
import com.alihasanov.courierpay.exception.NotFoundException;
import com.alihasanov.courierpay.repository.UserRepository;
import com.alihasanov.courierpay.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.alihasanov.courierpay.exception.CourierPayErrorResponse.EMAIL_ALREADY_REGISTERED;
import static com.alihasanov.courierpay.exception.CourierPayErrorResponse.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(EMAIL_ALREADY_REGISTERED);
        }
        var user = AppUser.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .role(request.role())
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(user.getEmail(), user.getRole().name()), "Bearer");
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        return new AuthResponse(jwtService.generateToken(user.getEmail(), user.getRole().name()), "Bearer");
    }
}
