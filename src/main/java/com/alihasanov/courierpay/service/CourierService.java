package com.alihasanov.courierpay.service;

import com.alihasanov.courierpay.entity.Courier;
import com.alihasanov.courierpay.exception.NotFoundException;
import com.alihasanov.courierpay.mapper.CourierMapper;
import com.alihasanov.courierpay.repository.CourierRepository;
import com.alihasanov.courierpay.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.alihasanov.courierpay.dto.CourierDtos.*;
import static com.alihasanov.courierpay.exception.CourierPayErrorResponse.COURIER_NOT_FOUND;
import static com.alihasanov.courierpay.exception.CourierPayErrorResponse.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CourierService {
    private final CourierRepository courierRepository;
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final BalanceService balanceService;
    private final CourierMapper courierMapper;

    @Transactional
    public CourierResponse create(CreateCourierRequest request) {
        var user = userRepository.findById(request.userId()).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        var company = companyService.get(request.companyId());
        var courier = courierRepository.save(Courier.builder()
                .user(user)
                .company(company)
                .phoneNumber(request.phoneNumber())
                .active(true)
                .build());
        balanceService.createInitialBalance(courier);
        return courierMapper.toResponse(courier);
    }

    public Courier get(Long id) {
        return courierRepository.findById(id).orElseThrow(() -> new NotFoundException(COURIER_NOT_FOUND));
    }

    public List<CourierResponse> findAll() {
        return courierRepository.findAll().stream().map(courierMapper::toResponse).toList();
    }

}
