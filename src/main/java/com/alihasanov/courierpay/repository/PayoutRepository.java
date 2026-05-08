package com.alihasanov.courierpay.repository;

import com.alihasanov.courierpay.enums.PayoutStatus;
import com.alihasanov.courierpay.entity.Payout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayoutRepository extends JpaRepository<Payout, Long> {
    List<Payout> findByStatusOrderByRequestedAtAsc(PayoutStatus status);
    List<Payout> findByCourierId(Long courierId);
}
