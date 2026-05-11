package com.alihasanov.courierpay.mapper;

import com.alihasanov.courierpay.dto.BalanceDtos.BalanceResponse;
import com.alihasanov.courierpay.entity.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BalanceMapper {
    @Mapping(target = "courierId", source = "courier.id")
    BalanceResponse toResponse(Balance balance);
}
