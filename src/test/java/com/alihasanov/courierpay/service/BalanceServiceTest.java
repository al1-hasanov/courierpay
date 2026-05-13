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
}
