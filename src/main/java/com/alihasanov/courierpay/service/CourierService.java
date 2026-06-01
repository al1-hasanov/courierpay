package com.alihasanov.courierpay.service;

import com.alihasanov.courierpay.entity.Courier;
import com.alihasanov.courierpay.enums.AuditAction;
import com.alihasanov.courierpay.exception.NotFoundException;
import com.alihasanov.courierpay.mapper.CourierMapper;
import com.alihasanov.courierpay.repository.CourierRepository;
import com.alihasanov.courierpay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private final AuditLogService auditLogService;

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
        auditLogService.success(
                AuditAction.CREATED_COURIER,
                "Courier",
                courier.getId(),
                "Courier created",
                "{\"userId\":" + user.getId() + ",\"companyId\":" + company.getId() + "}"
        );
        return courierMapper.toResponse(courier);
    }

    public Courier get(Long id) {
        return courierRepository.findById(id).orElseThrow(() -> new NotFoundException(COURIER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<CourierResponse> findAll(Long companyId, Boolean active, String fullName, Pageable pageable) {
        return courierRepository.findAll(buildSpecification(companyId, active, normalize(fullName)), pageable)
                .map(courierMapper::toResponse);
    }

    private Specification<Courier> buildSpecification(Long companyId, Boolean active, String fullName) {
        return (root, query, criteriaBuilder) -> {
            var predicate = criteriaBuilder.conjunction();
            if (companyId != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("company").get("id"), companyId));
            }
            if (active != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("active"), active));
            }
            if (fullName != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("user").<String>get("fullName")),
                        "%" + fullName.toLowerCase() + "%"
                ));
            }
            return predicate;
        };
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
