package com.alihasanov.courierpay.repository;

import com.alihasanov.courierpay.entity.Balance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
    Optional<Balance> findByCourierId(Long courierId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Balance b where b.courier.id = :courierId")
    Optional<Balance> findByCourierIdForUpdate(@Param("courierId") Long courierId);
}
