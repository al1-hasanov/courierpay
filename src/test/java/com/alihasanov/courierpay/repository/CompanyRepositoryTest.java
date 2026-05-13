package com.alihasanov.courierpay.repository;

import com.alihasanov.courierpay.entity.Company;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.liquibase.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void search_shouldFilterCompaniesByNameIgnoringCase() {
        companyRepository.save(Company.builder()
                .name("Bolt Food")
                .commissionRate(BigDecimal.valueOf(12.5))
                .build());
        companyRepository.save(Company.builder()
                .name("Wolt")
                .commissionRate(BigDecimal.valueOf(10))
                .build());
        companyRepository.flush();

        var result = companyRepository.search("bolt", PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("Bolt Food");
    }

    @Test
    void search_shouldReturnAllCompaniesWhenNameFilterIsNull() {
        companyRepository.save(Company.builder()
                .name("Bolt Food")
                .commissionRate(BigDecimal.valueOf(12.5))
                .build());
        companyRepository.save(Company.builder()
                .name("Wolt")
                .commissionRate(BigDecimal.valueOf(10))
                .build());
        companyRepository.flush();

        var result = companyRepository.search(null, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(Company::getName)
                .containsExactlyInAnyOrder("Bolt Food", "Wolt");
    }
}
