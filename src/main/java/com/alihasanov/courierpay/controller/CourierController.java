package com.alihasanov.courierpay.controller;

import com.alihasanov.courierpay.service.CourierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import static com.alihasanov.courierpay.dto.CourierDtos.*;

@RestController
@RequestMapping("/api/v1/couriers")
@RequiredArgsConstructor
public class CourierController {
    private final CourierService courierService;

    @PostMapping
    CourierResponse create(@Valid @RequestBody CreateCourierRequest request) { return courierService.create(request); }

    @GetMapping
    Page<CourierResponse> findAll(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String fullName,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        return courierService.findAll(companyId, active, fullName, pageable);
    }
}
