package com.alihasanov.courierpay.repository;

import com.alihasanov.courierpay.enums.EarningStatus;
import com.alihasanov.courierpay.entity.Earning;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EarningRepository extends JpaRepository<Earning, Long> {
    boolean existsByIdempotencyKey(String idempotencyKey);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Earning e where e.id = :earningId")
    Optional<Earning> findByIdForUpdate(@Param("earningId") Long earningId);

    List<Earning> findTop100ByStatusOrderByCreatedAtAsc(EarningStatus status);

    @Query("""
            select e from Earning e
            where (:courierId is null or e.courier.id = :courierId)
              and (:status is null or e.status = :status)
              and (:workDateFrom is null or e.workDate >= :workDateFrom)
              and (:workDateTo is null or e.workDate <= :workDateTo)
            """)
    Page<Earning> search(@Param("courierId") Long courierId,
                         @Param("status") EarningStatus status,
                         @Param("workDateFrom") LocalDate workDateFrom,
                         @Param("workDateTo") LocalDate workDateTo,
                         Pageable pageable);
}
