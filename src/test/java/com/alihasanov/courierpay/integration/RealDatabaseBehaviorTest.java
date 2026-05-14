package com.alihasanov.courierpay.integration;

import com.alihasanov.courierpay.entity.AppUser;
import com.alihasanov.courierpay.entity.Company;
import com.alihasanov.courierpay.enums.RoleName;
import com.alihasanov.courierpay.enums.UserStatus;
import com.alihasanov.courierpay.repository.CompanyRepository;
import com.alihasanov.courierpay.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
class RealDatabaseBehaviorTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("courierpay_test")
            .withUsername("courierpay")
            .withPassword("courierpay");

    @DynamicPropertySource
    static void registerPostgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.liquibase.enabled", () -> "true");
        registry.add("spring.sql.init.mode", () -> "never");
        registry.add("app.kafka.enabled", () -> "false");
    }

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void fullApplication_shouldStartWithPostgresContainerAndLiquibaseSchema() {
        Integer appliedChanges = jdbcTemplate.queryForObject(
                "select count(*) from databasechangelog",
                Integer.class
        );
        Integer shedlockTables = jdbcTemplate.queryForObject(
                "select count(*) from information_schema.tables where table_schema = 'public' and table_name = 'shedlock'",
                Integer.class
        );

        assertThat(appliedChanges).isNotNull().isPositive();
        assertThat(shedlockTables).isEqualTo(1);
    }

    @Test
    void companySearch_shouldUseRealPostgresDatabase() {
        companyRepository.save(Company.builder()
                .name("Bolt Food Testcontainers")
                .commissionRate(BigDecimal.valueOf(12.50))
                .build());
        companyRepository.save(Company.builder()
                .name("Wolt Testcontainers")
                .commissionRate(BigDecimal.valueOf(10.00))
                .build());
        companyRepository.flush();

        var result = companyRepository.search("bolt", PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Company::getName)
                .containsExactly("Bolt Food Testcontainers");
    }

    @Test
    void userEmailUniqueConstraint_shouldBeEnforcedByRealPostgresDatabase() {
        var firstUser = AppUser.builder()
                .email("real-db-user@example.com")
                .passwordHash("encoded-password")
                .fullName("Real DB User")
                .role(RoleName.ADMIN)
                .status(UserStatus.ACTIVE)
                .build();
        var duplicateUser = AppUser.builder()
                .email("real-db-user@example.com")
                .passwordHash("another-encoded-password")
                .fullName("Duplicate DB User")
                .role(RoleName.ADMIN)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.saveAndFlush(firstUser);

        assertThatThrownBy(() -> userRepository.saveAndFlush(duplicateUser))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
