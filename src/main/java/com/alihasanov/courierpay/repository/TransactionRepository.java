package com.alihasanov.courierpay.repository;

import com.alihasanov.courierpay.entity.Transaction;
import com.alihasanov.courierpay.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCourierIdOrderByCreatedAtDesc(Long courierId);

    @Query("""
            select t from Transaction t
            where (:courierId is null or t.courier.id = :courierId)
              and (:type is null or t.type = :type)
              and (:createdFrom is null or t.createdAt >= :createdFrom)
              and (:createdTo is null or t.createdAt <= :createdTo)
            order by t.createdAt desc
            """)
    List<Transaction> searchForExport(@Param("courierId") Long courierId,
                                      @Param("type") TransactionType type,
                                      @Param("createdFrom") Instant createdFrom,
                                      @Param("createdTo") Instant createdTo);
}
