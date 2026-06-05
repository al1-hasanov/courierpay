package com.alihasanov.courierpay.repository;

import com.alihasanov.courierpay.enums.PayoutStatus;
import com.alihasanov.courierpay.entity.Payout;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PayoutRepository extends JpaRepository<Payout, Long>, JpaSpecificationExecutor<Payout> {
    List<Payout> findByStatusOrderByRequestedAtAsc(PayoutStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Payout p where p.id = :id")
    Optional<Payout> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            select p from Payout p
            where (:courierId is null or p.courier.id = :courierId)
              and (:status is null or p.status = :status)
              and (:requestedFrom is null or p.requestedAt >= :requestedFrom)
              and (:requestedTo is null or p.requestedAt <= :requestedTo)
            """)
    Page<Payout> search(@Param("courierId") Long courierId,
                        @Param("status") PayoutStatus status,
                        @Param("requestedFrom") Instant requestedFrom,
                        @Param("requestedTo") Instant requestedTo,
                        Pageable pageable);
}
