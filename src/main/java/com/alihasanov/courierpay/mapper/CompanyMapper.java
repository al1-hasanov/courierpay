package com.alihasanov.courierpay.mapper;

import com.alihasanov.courierpay.dto.CompanyDtos.CompanyResponse;
import com.alihasanov.courierpay.entity.Company;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    CompanyResponse toResponse(Company company);
}
