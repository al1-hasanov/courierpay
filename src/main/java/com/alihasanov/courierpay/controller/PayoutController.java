package com.alihasanov.courierpay.controller;

import com.alihasanov.courierpay.service.PayoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.alihasanov.courierpay.dto.PayoutDtos.*;

@RestController
@RequestMapping("/api/v1/payouts")
@RequiredArgsConstructor
public class PayoutController {
    private final PayoutService payoutService;

    @PostMapping
    PayoutResponse request(@Valid @RequestBody RequestPayoutRequest request) { return payoutService.request(request); }

    @PostMapping("/{id}/approve")
    PayoutResponse approve(@PathVariable Long id) { return payoutService.approve(id); }

    @PostMapping("/{id}/reject")
    PayoutResponse reject(@PathVariable Long id) { return payoutService.reject(id); }

    @GetMapping
    List<PayoutResponse> findAll() { return payoutService.findAll(); }
}
