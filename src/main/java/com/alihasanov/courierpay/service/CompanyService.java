package com.alihasanov.courierpay.service;

import com.alihasanov.courierpay.entity.Company;
import com.alihasanov.courierpay.enums.AuditAction;
import com.alihasanov.courierpay.exception.NotFoundException;
import com.alihasanov.courierpay.mapper.CompanyMapper;
import com.alihasanov.courierpay.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.alihasanov.courierpay.dto.CompanyDtos.*;
import static com.alihasanov.courierpay.exception.CourierPayErrorResponse.COMPANY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final AuditLogService auditLogService;

    public CompanyResponse create(CreateCompanyRequest request) {
        var company = companyRepository.save(Company.builder()
                .name(request.name())
                .commissionRate(request.commissionRate())
                .build());
        auditLogService.success(
                AuditAction.CREATED_COMPANY,
                "Company",
                company.getId(),
                "Company created",
                "{\"name\":\"" + company.getName() + "\",\"commissionRate\":" + company.getCommissionRate() + "}"
        );
        return companyMapper.toResponse(company);
    }

    public Page<CompanyResponse> findAll(String name, Pageable pageable) {
        return companyRepository.findAll(buildSpecification(normalize(name)), pageable).map(companyMapper::toResponse);
    }

    public Company get(Long id) {
        return companyRepository.findById(id).orElseThrow(() -> new NotFoundException(COMPANY_NOT_FOUND));
    }

    private Specification<Company> buildSpecification(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.<String>get("name")),
                    "%" + name.toLowerCase() + "%"
            );
        };
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
