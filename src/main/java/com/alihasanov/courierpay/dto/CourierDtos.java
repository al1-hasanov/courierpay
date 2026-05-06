package com.alihasanov.courierpay.dto;

import jakarta.validation.constraints.NotNull;

public class CourierDtos {
    public record CreateCourierRequest(@NotNull Long userId, @NotNull Long companyId, String phoneNumber) {}
    public record CourierResponse(Long id, Long userId, String fullName, Long companyId, String companyName, String phoneNumber, boolean active) {}
}
