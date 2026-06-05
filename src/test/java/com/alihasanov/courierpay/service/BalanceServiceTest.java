package com.alihasanov.courierpay.service;

import com.alihasanov.courierpay.entity.Balance;
import com.alihasanov.courierpay.exception.BusinessException;
import com.alihasanov.courierpay.repository.BalanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static com.alihasanov.courierpay.exception.CourierPayErrorResponse.INSUFFICIENT_AVAILABLE_BALANCE;
import static com.alihasanov.courierpay.exception.CourierPayErrorResponse.INSUFFICIENT_RESERVED_BALANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private CourierAccessService courierAccessService;

    @InjectMocks
    private BalanceService balanceService;

    @Test
    void getByCourierId_shouldCheckAccessAndReturnBalance() {
        var balance = Balance.builder()
                .availableAmount(new BigDecimal("42.50"))
                .reservedAmount(BigDecimal.ZERO)
                .build();
        when(balanceRepository.findByCourierId(7L)).thenReturn(Optional.of(balance));

        Balance result = balanceService.getByCourierId(7L);

        assertThat(result).isSameAs(balance);
        verify(courierAccessService).assertCanAccessCourier(7L);
    }

    @Test
    void credit_shouldIncreaseAvailableAmount() {
        var balance = Balance.builder()
                .availableAmount(new BigDecimal("10.00"))
                .reservedAmount(BigDecimal.ZERO)
                .build();
        when(balanceRepository.findByCourierIdForUpdate(7L)).thenReturn(Optional.of(balance));

        Balance result = balanceService.credit(7L, new BigDecimal("5.25"));

        assertThat(result.getAvailableAmount()).isEqualByComparingTo("15.25");
    }

    @Test
    void debit_shouldRejectAmountGreaterThanAvailableBalance() {
        var balance = Balance.builder()
                .availableAmount(new BigDecimal("10.00"))
                .reservedAmount(BigDecimal.ZERO)
                .build();
        when(balanceRepository.findByCourierIdForUpdate(7L)).thenReturn(Optional.of(balance));

        assertThatThrownBy(() -> balanceService.debit(7L, new BigDecimal("10.01")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorResponse")
                .isEqualTo(INSUFFICIENT_AVAILABLE_BALANCE);

        assertThat(balance.getAvailableAmount()).isEqualByComparingTo("10.00");
    }

    @Test
    void reserve_shouldMoveAmountFromAvailableToReserved() {
        var balance = Balance.builder()
                .availableAmount(new BigDecimal("100.00"))
                .reservedAmount(new BigDecimal("20.00"))
                .build();
        when(balanceRepository.findByCourierIdForUpdate(7L)).thenReturn(Optional.of(balance));

        Balance result = balanceService.reserve(7L, new BigDecimal("30.00"));

        assertThat(result.getAvailableAmount()).isEqualByComparingTo("70.00");
        assertThat(result.getReservedAmount()).isEqualByComparingTo("50.00");
    }

    @Test
    void reserve_shouldRejectAmountGreaterThanAvailableBalance() {
        var balance = Balance.builder()
                .availableAmount(new BigDecimal("10.00"))
                .reservedAmount(BigDecimal.ZERO)
                .build();
        when(balanceRepository.findByCourierIdForUpdate(7L)).thenReturn(Optional.of(balance));

        assertThatThrownBy(() -> balanceService.reserve(7L, new BigDecimal("10.01")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorResponse")
                .isEqualTo(INSUFFICIENT_AVAILABLE_BALANCE);

        assertThat(balance.getAvailableAmount()).isEqualByComparingTo("10.00");
        assertThat(balance.getReservedAmount()).isEqualByComparingTo("0.00");
    }

    @Test
    void releaseReserved_shouldMoveAmountFromReservedBackToAvailable() {
        var balance = Balance.builder()
                .availableAmount(new BigDecimal("70.00"))
                .reservedAmount(new BigDecimal("30.00"))
                .build();
        when(balanceRepository.findByCourierIdForUpdate(7L)).thenReturn(Optional.of(balance));

        Balance result = balanceService.releaseReserved(7L, new BigDecimal("10.00"));

        assertThat(result.getAvailableAmount()).isEqualByComparingTo("80.00");
        assertThat(result.getReservedAmount()).isEqualByComparingTo("20.00");
    }

    @Test
    void consumeReserved_shouldDecreaseOnlyReservedAmount() {
        var balance = Balance.builder()
                .availableAmount(new BigDecimal("70.00"))
                .reservedAmount(new BigDecimal("30.00"))
                .build();
        when(balanceRepository.findByCourierIdForUpdate(7L)).thenReturn(Optional.of(balance));

        Balance result = balanceService.consumeReserved(7L, new BigDecimal("15.00"));

        assertThat(result.getAvailableAmount()).isEqualByComparingTo("70.00");
        assertThat(result.getReservedAmount()).isEqualByComparingTo("15.00");
    }

    @Test
    void consumeReserved_shouldRejectAmountGreaterThanReservedBalance() {
        var balance = Balance.builder()
                .availableAmount(new BigDecimal("70.00"))
                .reservedAmount(new BigDecimal("5.00"))
                .build();
        when(balanceRepository.findByCourierIdForUpdate(7L)).thenReturn(Optional.of(balance));

        assertThatThrownBy(() -> balanceService.consumeReserved(7L, new BigDecimal("5.01")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorResponse")
                .isEqualTo(INSUFFICIENT_RESERVED_BALANCE);

        assertThat(balance.getAvailableAmount()).isEqualByComparingTo("70.00");
        assertThat(balance.getReservedAmount()).isEqualByComparingTo("5.00");
    }
}
