package com.alihasanov.courierpay.controller;

import com.alihasanov.courierpay.service.EarningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    List<EarningResponse> findAll() { return earningService.findAll(); }
}
