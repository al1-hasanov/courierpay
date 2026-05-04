package com.alihasanov.courierpay.controller;

import com.alihasanov.courierpay.service.CourierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.alihasanov.courierpay.dto.CourierDtos.*;

@RestController
@RequestMapping("/api/v1/couriers")
@RequiredArgsConstructor
public class CourierController {
    private final CourierService courierService;

    @PostMapping
    CourierResponse create(@Valid @RequestBody CreateCourierRequest request) { return courierService.create(request); }

    @GetMapping
    List<CourierResponse> findAll() { return courierService.findAll(); }
}
