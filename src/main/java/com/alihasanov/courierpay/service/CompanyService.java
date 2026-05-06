package com.alihasanov.courierpay.service;

import com.alihasanov.courierpay.entity.Company;
import com.alihasanov.courierpay.exception.NotFoundException;
import com.alihasanov.courierpay.repository.CompanyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.alihasanov.courierpay.dto.CompanyDtos.*;
import static com.alihasanov.courierpay.exception.CourierPayErrorResponse.COMPANY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyResponse create(CreateCompanyRequest request) {
        var company = companyRepository.save(Company.builder()
                .name(request.name())
                .commissionRate(request.commissionRate())
                .build());
        return toResponse(company);
    }

    public List<CompanyResponse> findAll() {
        return companyRepository.findAll().stream().map(this::toResponse).toList();
    }

    public Company get(Long id) {
        return companyRepository.findById(id).orElseThrow(() -> new NotFoundException(COMPANY_NOT_FOUND));
    }

    private CompanyResponse toResponse(Company c) { return new CompanyResponse(c.getId(), c.getName(), c.getCommissionRate()); }
}
