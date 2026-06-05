package com.alihasanov.courierpay.integration;

import com.alihasanov.courierpay.dto.CourierDtos.CreateCourierRequest;
import com.alihasanov.courierpay.dto.EarningDtos.CreateEarningRequest;
import com.alihasanov.courierpay.dto.PayoutDtos.RequestPayoutRequest;
import com.alihasanov.courierpay.entity.AppUser;
import com.alihasanov.courierpay.entity.Company;
import com.alihasanov.courierpay.entity.Transaction;
import com.alihasanov.courierpay.enums.EarningStatus;
import com.alihasanov.courierpay.enums.PayoutStatus;
import com.alihasanov.courierpay.enums.RoleName;
import com.alihasanov.courierpay.enums.TransactionType;
import com.alihasanov.courierpay.enums.UserStatus;
import com.alihasanov.courierpay.exception.BusinessException;
import com.alihasanov.courierpay.repository.BalanceRepository;
import com.alihasanov.courierpay.repository.CompanyRepository;
import com.alihasanov.courierpay.repository.EarningRepository;
import com.alihasanov.courierpay.repository.TransactionRepository;
import com.alihasanov.courierpay.repository.UserRepository;
import com.alihasanov.courierpay.service.CourierService;
import com.alihasanov.courierpay.service.EarningService;
import com.alihasanov.courierpay.service.PayoutService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@Transactional
class BalancePayoutWorkflowIntegrationTest {

    private static final String ADMIN_EMAIL = "balance-payout-admin@example.com";

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("courierpay_balance_payout_test")
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
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private EarningRepository earningRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CourierService courierService;

    @Autowired
    private EarningService earningService;

    @Autowired
    private PayoutService payoutService;

    @BeforeEach
    void authenticateAsAdmin() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                ADMIN_EMAIL,
                "test-password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        ));

        userRepository.save(AppUser.builder()
                .email(ADMIN_EMAIL)
                .passwordHash("encoded-password")
                .fullName("Balance Payout Admin")
                .role(RoleName.ADMIN)
                .status(UserStatus.ACTIVE)
                .build());
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void earningProcessing_shouldCreditBalanceAndRecordTransactionsAgainstRealPostgres() {
        var fixture = createCourierFixture("earning-workflow");

        var initialBalance = balanceRepository.findByCourierId(fixture.courierId()).orElseThrow();
        assertThat(initialBalance.getAvailableAmount()).isEqualByComparingTo("0.00");
        assertThat(initialBalance.getReservedAmount()).isEqualByComparingTo("0.00");

        var earning = earningService.create(new CreateEarningRequest(
                fixture.courierId(),
                BigDecimal.valueOf(100.00),
                LocalDate.now(),
                "earning-" + UUID.randomUUID()
        ));

        assertThat(earning.status()).isEqualTo(EarningStatus.PENDING);
        assertThat(earning.commissionAmount()).isEqualByComparingTo("10.00");
        assertThat(earning.netAmount()).isEqualByComparingTo("90.00");

        earningService.process(earning.id());
        earningService.process(earning.id());

        var processedEarning = earningRepository.findById(earning.id()).orElseThrow();
        var updatedBalance = balanceRepository.findByCourierId(fixture.courierId()).orElseThrow();
        var transactions = transactionRepository.findByCourierIdOrderByCreatedAtDesc(fixture.courierId());

        assertThat(processedEarning.getStatus()).isEqualTo(EarningStatus.PROCESSED);
        assertThat(processedEarning.getProcessedAt()).isNotNull();
        assertThat(updatedBalance.getAvailableAmount()).isEqualByComparingTo("90.00");
        assertThat(updatedBalance.getReservedAmount()).isEqualByComparingTo("0.00");
        assertThat(transactions).hasSize(2);
        assertTransaction(transactions, TransactionType.EARNING_CREDIT, earning.id(), "90.00");
        assertTransaction(transactions, TransactionType.COMMISSION_DEBIT, earning.id(), "10.00");
    }

    @Test
    void payoutWorkflow_shouldRequestApproveDebitBalanceAndRejectOverdrawAgainstRealPostgres() {
        var fixture = createCourierFixture("payout-workflow");
        var earning = earningService.create(new CreateEarningRequest(
                fixture.courierId(),
                BigDecimal.valueOf(200.00),
                LocalDate.now(),
                "earning-" + UUID.randomUUID()
        ));
        earningService.process(earning.id());

        assertThat(balanceRepository.findByCourierId(fixture.courierId()).orElseThrow().getAvailableAmount())
                .isEqualByComparingTo("180.00");

        var requestedPayout = payoutService.request(new RequestPayoutRequest(
                fixture.courierId(),
                BigDecimal.valueOf(80.00)
        ));

        assertThat(requestedPayout.status()).isEqualTo(PayoutStatus.REQUESTED);
        var balanceAfterRequest = balanceRepository.findByCourierId(fixture.courierId()).orElseThrow();
        assertThat(balanceAfterRequest.getAvailableAmount()).isEqualByComparingTo("100.00");
        assertThat(balanceAfterRequest.getReservedAmount()).isEqualByComparingTo("80.00");

        var completedPayout = payoutService.approve(requestedPayout.id());

        var finalBalance = balanceRepository.findByCourierId(fixture.courierId()).orElseThrow();
        var transactions = transactionRepository.findByCourierIdOrderByCreatedAtDesc(fixture.courierId());

        assertThat(completedPayout.status()).isEqualTo(PayoutStatus.COMPLETED);
        assertThat(finalBalance.getAvailableAmount()).isEqualByComparingTo("100.00");
        assertThat(finalBalance.getReservedAmount()).isEqualByComparingTo("0.00");
        assertTransaction(transactions, TransactionType.PAYOUT_DEBIT, requestedPayout.id(), "80.00");

        assertThatThrownBy(() -> payoutService.request(new RequestPayoutRequest(
                fixture.courierId(),
                BigDecimal.valueOf(101.00)
        ))).isInstanceOf(BusinessException.class);
    }

    @Test
    void payoutReject_shouldReleaseReservedAmountBackToAvailableBalance() {
        var fixture = createCourierFixture("payout-reject-workflow");
        var earning = earningService.create(new CreateEarningRequest(
                fixture.courierId(),
                BigDecimal.valueOf(100.00),
                LocalDate.now(),
                "earning-" + UUID.randomUUID()
        ));
        earningService.process(earning.id());

        var requestedPayout = payoutService.request(new RequestPayoutRequest(
                fixture.courierId(),
                BigDecimal.valueOf(50.00)
        ));

        var balanceAfterRequest = balanceRepository.findByCourierId(fixture.courierId()).orElseThrow();
        assertThat(balanceAfterRequest.getAvailableAmount()).isEqualByComparingTo("40.00");
        assertThat(balanceAfterRequest.getReservedAmount()).isEqualByComparingTo("50.00");

        var rejectedPayout = payoutService.reject(requestedPayout.id());

        var balanceAfterReject = balanceRepository.findByCourierId(fixture.courierId()).orElseThrow();
        assertThat(rejectedPayout.status()).isEqualTo(PayoutStatus.REJECTED);
        assertThat(balanceAfterReject.getAvailableAmount()).isEqualByComparingTo("90.00");
        assertThat(balanceAfterReject.getReservedAmount()).isEqualByComparingTo("0.00");
    }

    private CourierFixture createCourierFixture(String label) {
        var unique = label + "-" + UUID.randomUUID();
        var company = companyRepository.save(Company.builder()
                .name("Testcontainers " + unique)
                .commissionRate(BigDecimal.valueOf(10.00))
                .build());
        var courierUser = userRepository.save(AppUser.builder()
                .email(unique + "@example.com")
                .passwordHash("encoded-password")
                .fullName("Courier " + unique)
                .role(RoleName.COURIER)
                .status(UserStatus.ACTIVE)
                .build());

        var courier = courierService.create(new CreateCourierRequest(
                courierUser.getId(),
                company.getId(),
                "+994501234567"
        ));

        return new CourierFixture(courier.id());
    }

    private void assertTransaction(List<Transaction> transactions, TransactionType type, Long referenceId, String amount) {
        assertThat(transactions)
                .filteredOn(transaction -> transaction.getType() == type && referenceId.equals(transaction.getReferenceId()))
                .singleElement()
                .satisfies(transaction -> assertThat(transaction.getAmount()).isEqualByComparingTo(amount));
    }

    private record CourierFixture(Long courierId) {
    }
}
