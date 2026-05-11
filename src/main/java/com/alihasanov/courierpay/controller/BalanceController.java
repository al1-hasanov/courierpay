package com.alihasanov.courierpay.controller;

import com.alihasanov.courierpay.dto.BalanceDtos.BalanceResponse;
import com.alihasanov.courierpay.mapper.BalanceMapper;
import com.alihasanov.courierpay.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/balances")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;
    private final BalanceMapper balanceMapper;

    @GetMapping("/couriers/{courierId}")
    BalanceResponse getByCourier(@PathVariable Long courierId) {
        var balance = balanceService.getByCourierId(courierId);
        return balanceMapper.toResponse(balance);
    }
}
