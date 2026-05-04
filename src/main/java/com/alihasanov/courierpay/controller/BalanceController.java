package com.alihasanov.courierpay.controller;

import com.alihasanov.courierpay.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/balances")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;

    @GetMapping("/couriers/{courierId}")
    BalanceResponse getByCourier(@PathVariable Long courierId) {
        var balance = balanceService.getByCourierId(courierId);
        return new BalanceResponse(balance.getCourier().getId(), balance.getAvailableAmount(), balance.getReservedAmount());
    }

    public record BalanceResponse(Long courierId, BigDecimal availableAmount, BigDecimal reservedAmount) {}
}
