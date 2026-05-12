package com.alihasanov.courierpay.service;

import com.alihasanov.courierpay.entity.Courier;
import com.alihasanov.courierpay.entity.Transaction;
import com.alihasanov.courierpay.enums.TransactionType;
import com.alihasanov.courierpay.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void record_shouldSaveTransactionWithProvidedDetails() {
        var courier = Courier.builder().id(3L).build();
        var amount = new BigDecimal("19.99");
        var captor = ArgumentCaptor.forClass(Transaction.class);

        transactionService.record(
                courier,
                TransactionType.PAYOUT_DEBIT,
                amount,
                55L,
                "Payout completed internally"
        );

        verify(transactionRepository).save(captor.capture());
        Transaction saved = captor.getValue();
        assertThat(saved.getCourier()).isSameAs(courier);
        assertThat(saved.getType()).isEqualTo(TransactionType.PAYOUT_DEBIT);
        assertThat(saved.getAmount()).isEqualByComparingTo(amount);
        assertThat(saved.getReferenceId()).isEqualTo(55L);
        assertThat(saved.getDescription()).isEqualTo("Payout completed internally");
    }
}
