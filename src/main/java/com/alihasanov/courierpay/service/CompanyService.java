package com.alihasanov.courierpay.service;

import com.alihasanov.courierpay.exception.NotFoundException;
import com.alihasanov.courierpay.repository.CompanyRepository;
import com.alihasanov.courierpay.entity.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.alihasanov.courierpay.dto.CompanyDtos.*;

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
        return companyRepository.findById(id).orElseThrow(() -> new NotFoundException("Company not found"));
    }

    private CompanyResponse toResponse(Company c) { return new CompanyResponse(c.getId(), c.getName(), c.getCommissionRate()); }
}
