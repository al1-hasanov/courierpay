package com.alihasanov.courierpay.service;

import com.alihasanov.courierpay.entity.AppUser;
import com.alihasanov.courierpay.entity.RefreshToken;
import com.alihasanov.courierpay.exception.BusinessException;
import com.alihasanov.courierpay.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

import static com.alihasanov.courierpay.exception.CourierPayErrorResponse.INVALID_REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final int TOKEN_BYTES_LENGTH = 64;

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.security.refresh-token-expiration-days}")
    private long refreshTokenExpirationDays;

    @Transactional
    public RefreshToken create(AppUser user) {
        var refreshToken = RefreshToken.builder()
                .token(generateSecureToken())
                .user(user)
                .expiresAt(Instant.now().plusSeconds(refreshTokenExpirationDays * 24 * 60 * 60))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken validate(String token) {
        var refreshToken = refreshTokenRepository.findByTokenForUpdate(token)
                .orElseThrow(() -> new BusinessException(INVALID_REFRESH_TOKEN));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException(INVALID_REFRESH_TOKEN);
        }

        return refreshToken;
    }

    @Transactional
    public void revoke(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[TOKEN_BYTES_LENGTH];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
