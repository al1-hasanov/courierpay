package com.alihasanov.courierpay.dto;

import java.math.BigDecimal;

public class BalanceDtos {
    public record BalanceResponse(Long courierId, BigDecimal availableAmount, BigDecimal reservedAmount) {}
}
