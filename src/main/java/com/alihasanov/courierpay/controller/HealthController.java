package com.alihasanov.courierpay.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
                "service", "courierpay",
                "status", "UP",
                "message", "CourierPay API is running"
        );
    }

    @GetMapping("/healthz")
    public Map<String, String> health() {
        return Map.of(
                "service", "courierpay",
                "status", "UP"
        );
    }
}
