package com.alihasanov.courierpay.mapper;

import com.alihasanov.courierpay.dto.CourierDtos.CourierResponse;
import com.alihasanov.courierpay.entity.Courier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourierMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "fullName", source = "user.fullName")
    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "companyName", source = "company.name")
    CourierResponse toResponse(Courier courier);
}
