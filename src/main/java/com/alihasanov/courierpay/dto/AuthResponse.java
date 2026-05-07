package com.alihasanov.courierpay.dto;

public record AuthResponse(String accessToken, String refreshToken, String tokenType) {}
