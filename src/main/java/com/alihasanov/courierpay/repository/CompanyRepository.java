package com.alihasanov.courierpay.repository;

import com.alihasanov.courierpay.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    @Query("""
            select c from Company c
            where (:name is null or lower(c.name) like lower(concat('%', :name, '%')))
            """)
    Page<Company> search(@Param("name") String name, Pageable pageable);
}
