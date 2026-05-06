package com.alihasanov.courierpay.service;

import com.alihasanov.courierpay.entity.Balance;
import com.alihasanov.courierpay.entity.Courier;
import com.alihasanov.courierpay.exception.BusinessException;
import com.alihasanov.courierpay.exception.NotFoundException;
import com.alihasanov.courierpay.repository.BalanceRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.alihasanov.courierpay.exception.CourierPayErrorResponse.BALANCE_NOT_FOUND;
import static com.alihasanov.courierpay.exception.CourierPayErrorResponse.INSUFFICIENT_AVAILABLE_BALANCE;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final BalanceRepository balanceRepository;

    public void createInitialBalance(Courier courier) {
        balanceRepository.save(Balance.builder()
                .courier(courier)
                .availableAmount(BigDecimal.ZERO)
                .reservedAmount(BigDecimal.ZERO)
                .build());
    }

    public Balance getByCourierId(Long courierId) {
        return balanceRepository.findByCourierId(courierId)
                .orElseThrow(() -> new NotFoundException(BALANCE_NOT_FOUND));
    }

    public Balance credit(Long courierId, BigDecimal amount) {
        var balance = balanceRepository.findByCourierIdForUpdate(courierId)
                .orElseThrow(() -> new NotFoundException(BALANCE_NOT_FOUND));
        balance.setAvailableAmount(balance.getAvailableAmount().add(amount));
        return balance;
    }

    public Balance debit(Long courierId, BigDecimal amount) {
        var balance = balanceRepository.findByCourierIdForUpdate(courierId)
                .orElseThrow(() -> new NotFoundException(BALANCE_NOT_FOUND));
        if (balance.getAvailableAmount().compareTo(amount) < 0) {
            throw new BusinessException(INSUFFICIENT_AVAILABLE_BALANCE);
        }
        balance.setAvailableAmount(balance.getAvailableAmount().subtract(amount));
        return balance;
    }
}
