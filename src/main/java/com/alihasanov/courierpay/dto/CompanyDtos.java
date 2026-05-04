package com.alihasanov.courierpay.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CompanyDtos {
    public record CreateCompanyRequest(
            @NotBlank String name,
            @NotNull @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal commissionRate
    ) {}
    public record CompanyResponse(Long id, String name, BigDecimal commissionRate) {}
}
