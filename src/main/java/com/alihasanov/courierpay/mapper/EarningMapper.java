package com.alihasanov.courierpay.mapper;

import com.alihasanov.courierpay.dto.EarningDtos.EarningResponse;
import com.alihasanov.courierpay.entity.Earning;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EarningMapper {
    @Mapping(target = "courierId", source = "courier.id")
    EarningResponse toResponse(Earning earning);
}
