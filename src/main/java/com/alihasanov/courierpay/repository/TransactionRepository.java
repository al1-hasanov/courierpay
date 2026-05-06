package com.alihasanov.courierpay.repository;

import com.alihasanov.courierpay.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCourierIdOrderByCreatedAtDesc(Long courierId);
}
