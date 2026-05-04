package com.alihasanov.courierpay.service;

import com.alihasanov.courierpay.repository.CourierRepository;
import com.alihasanov.courierpay.exception.NotFoundException;
import com.alihasanov.courierpay.entity.Courier;
import com.alihasanov.courierpay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.alihasanov.courierpay.dto.CourierDtos.*;

@Service
@RequiredArgsConstructor
public class CourierService {
    private final CourierRepository courierRepository;
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final BalanceService balanceService;

    @Transactional
    public CourierResponse create(CreateCourierRequest request) {
        var user = userRepository.findById(request.userId()).orElseThrow(() -> new NotFoundException("User not found"));
        var company = companyService.get(request.companyId());
        var courier = courierRepository.save(Courier.builder()
                .user(user)
                .company(company)
                .phoneNumber(request.phoneNumber())
                .active(true)
                .build());
        balanceService.createInitialBalance(courier);
        return toResponse(courier);
    }

    public Courier get(Long id) {
        return courierRepository.findById(id).orElseThrow(() -> new NotFoundException("Courier not found"));
    }

    public List<CourierResponse> findAll() {
        return courierRepository.findAll().stream().map(this::toResponse).toList();
    }

    private CourierResponse toResponse(Courier c) {
        return new CourierResponse(c.getId(), c.getUser().getId(), c.getUser().getFullName(), c.getCompany().getId(), c.getCompany().getName(), c.getPhoneNumber(), c.isActive());
    }
}
