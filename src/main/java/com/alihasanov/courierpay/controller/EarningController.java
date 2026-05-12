package com.alihasanov.courierpay.controller;

import com.alihasanov.courierpay.enums.EarningStatus;
import com.alihasanov.courierpay.service.EarningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.alihasanov.courierpay.dto.EarningDtos.*;

@RestController
@RequestMapping("/api/v1/earnings")
@RequiredArgsConstructor
public class EarningController {
    private final EarningService earningService;

    @PostMapping
    EarningResponse create(@Valid @RequestBody CreateEarningRequest request) { return earningService.create(request); }

    @PostMapping("/{id}/process")
    void process(@PathVariable Long id) { earningService.process(id); }

    @GetMapping
    Page<EarningResponse> findAll(
            @RequestParam(required = false) Long courierId,
            @RequestParam(required = false) EarningStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDateTo,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        return earningService.findAll(courierId, status, workDateFrom, workDateTo, pageable);
    }
}
