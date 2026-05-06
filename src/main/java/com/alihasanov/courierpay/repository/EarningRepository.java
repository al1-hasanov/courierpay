package com.alihasanov.courierpay.repository;

import com.alihasanov.courierpay.enums.EarningStatus;
import com.alihasanov.courierpay.entity.Earning;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EarningRepository extends JpaRepository<Earning, Long> {
    boolean existsByIdempotencyKey(String idempotencyKey);
    List<Earning> findTop100ByStatusOrderByCreatedAtAsc(EarningStatus status);
}
