package com.alihasanov.courierpay.controller;

import com.alihasanov.courierpay.enums.PayoutStatus;
import com.alihasanov.courierpay.service.PayoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

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
    Page<PayoutResponse> findAll(
            @RequestParam(required = false) Long courierId,
            @RequestParam(required = false) PayoutStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant requestedFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant requestedTo,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        return payoutService.findAll(courierId, status, requestedFrom, requestedTo, pageable);
    }
}
