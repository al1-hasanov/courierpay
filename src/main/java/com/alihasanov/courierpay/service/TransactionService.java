package com.alihasanov.courierpay.service;

import com.alihasanov.courierpay.enums.TransactionType;
import com.alihasanov.courierpay.entity.Courier;
import com.alihasanov.courierpay.entity.Transaction;
import com.alihasanov.courierpay.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public void record(Courier courier, TransactionType type, BigDecimal amount, Long referenceId, String description) {
        transactionRepository.save(Transaction.builder()
                .courier(courier)
                .type(type)
                .amount(amount)
                .referenceId(referenceId)
                .description(description)
                .build());
    }
}
