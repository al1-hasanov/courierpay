package com.alihasanov.courierpay.dto;

import com.alihasanov.courierpay.enums.EarningStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EarningDtos {
    public record CreateEarningRequest(
            @NotNull Long courierId,
            @NotNull @DecimalMin("0.01") BigDecimal grossAmount,
            @NotNull LocalDate workDate,
            @NotBlank String idempotencyKey
    ) {}
    public record EarningResponse(Long id, Long courierId, BigDecimal grossAmount, BigDecimal commissionAmount,
                                  BigDecimal netAmount, EarningStatus status, LocalDate workDate) {}
}
