package com.alihasanov.courierpay.dto;

import com.alihasanov.courierpay.enums.PayoutStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PayoutDtos {
    public record RequestPayoutRequest(@NotNull Long courierId, @NotNull @DecimalMin("0.01") BigDecimal amount) {}
    public record PayoutResponse(Long id, Long courierId, BigDecimal amount, PayoutStatus status) {}
}
