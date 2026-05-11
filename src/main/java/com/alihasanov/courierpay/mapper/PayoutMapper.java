package com.alihasanov.courierpay.mapper;

import com.alihasanov.courierpay.dto.PayoutDtos.PayoutResponse;
import com.alihasanov.courierpay.entity.Payout;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PayoutMapper {
    @Mapping(target = "courierId", source = "courier.id")
    PayoutResponse toResponse(Payout payout);
}
