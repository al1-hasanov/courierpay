package com.alihasanov.courierpay.repository;

import com.alihasanov.courierpay.entity.Courier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourierRepository extends JpaRepository<Courier, Long> {
    Optional<Courier> findByUserEmail(String email);

    @Query("""
            select c from Courier c
            join c.user u
            join c.company co
            where (:companyId is null or co.id = :companyId)
              and (:active is null or c.active = :active)
              and (:fullName is null or lower(u.fullName) like lower(concat('%', :fullName, '%')))
            """)
    Page<Courier> search(@Param("companyId") Long companyId,
                         @Param("active") Boolean active,
                         @Param("fullName") String fullName,
                         Pageable pageable);
}
