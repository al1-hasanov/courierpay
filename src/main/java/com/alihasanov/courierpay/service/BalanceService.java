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
import static com.alihasanov.courierpay.exception.CourierPayErrorResponse.INSUFFICIENT_RESERVED_BALANCE;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final BalanceRepository balanceRepository;
    private final CourierAccessService courierAccessService;

    public void createInitialBalance(Courier courier) {
        balanceRepository.save(Balance.builder()
                .courier(courier)
                .availableAmount(BigDecimal.ZERO)
                .reservedAmount(BigDecimal.ZERO)
                .build());
    }

    public Balance getByCourierId(Long courierId) {
        courierAccessService.assertCanAccessCourier(courierId);
        return balanceRepository.findByCourierId(courierId)
                .orElseThrow(() -> new NotFoundException(BALANCE_NOT_FOUND));
    }

    public Balance credit(Long courierId, BigDecimal amount) {
        var balance = getByCourierIdForUpdate(courierId);
        balance.setAvailableAmount(balance.getAvailableAmount().add(amount));
        return balance;
    }

    public Balance debit(Long courierId, BigDecimal amount) {
        var balance = getByCourierIdForUpdate(courierId);
        assertAvailableAmount(balance, amount);
        balance.setAvailableAmount(balance.getAvailableAmount().subtract(amount));
        return balance;
    }

    public Balance reserve(Long courierId, BigDecimal amount) {
        var balance = getByCourierIdForUpdate(courierId);
        assertAvailableAmount(balance, amount);
        balance.setAvailableAmount(balance.getAvailableAmount().subtract(amount));
        balance.setReservedAmount(balance.getReservedAmount().add(amount));
        return balance;
    }

    public Balance releaseReserved(Long courierId, BigDecimal amount) {
        var balance = getByCourierIdForUpdate(courierId);
        assertReservedAmount(balance, amount);
        balance.setReservedAmount(balance.getReservedAmount().subtract(amount));
        balance.setAvailableAmount(balance.getAvailableAmount().add(amount));
        return balance;
    }

    public Balance consumeReserved(Long courierId, BigDecimal amount) {
        var balance = getByCourierIdForUpdate(courierId);
        assertReservedAmount(balance, amount);
        balance.setReservedAmount(balance.getReservedAmount().subtract(amount));
        return balance;
    }

    private Balance getByCourierIdForUpdate(Long courierId) {
        return balanceRepository.findByCourierIdForUpdate(courierId)
                .orElseThrow(() -> new NotFoundException(BALANCE_NOT_FOUND));
    }

    private void assertAvailableAmount(Balance balance, BigDecimal amount) {
        if (balance.getAvailableAmount().compareTo(amount) < 0) {
            throw new BusinessException(INSUFFICIENT_AVAILABLE_BALANCE);
        }
    }

    private void assertReservedAmount(Balance balance, BigDecimal amount) {
        if (balance.getReservedAmount().compareTo(amount) < 0) {
            throw new BusinessException(INSUFFICIENT_RESERVED_BALANCE);
        }
    }
}
