package com.alihasanov.courierpay.dto;

import com.alihasanov.courierpay.enums.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 6) String password,
        @NotBlank String fullName,
        @NotNull RoleName role
) {}
