package com.alihasanov.courierpay;

import com.alihasanov.courierpay.config.SecurityConfig;
import com.alihasanov.courierpay.config.ShedLockConfig;
import com.alihasanov.courierpay.controller.AuthController;
import com.alihasanov.courierpay.controller.BalanceController;
import com.alihasanov.courierpay.controller.CompanyController;
import com.alihasanov.courierpay.controller.CourierController;
import com.alihasanov.courierpay.controller.EarningController;
import com.alihasanov.courierpay.controller.HealthController;
import com.alihasanov.courierpay.controller.PayoutController;
import com.alihasanov.courierpay.controller.ReportController;
import com.alihasanov.courierpay.repository.BalanceRepository;
import com.alihasanov.courierpay.repository.CompanyRepository;
import com.alihasanov.courierpay.repository.CourierRepository;
import com.alihasanov.courierpay.repository.EarningRepository;
import com.alihasanov.courierpay.repository.PayoutRepository;
import com.alihasanov.courierpay.repository.RefreshTokenRepository;
import com.alihasanov.courierpay.repository.TransactionRepository;
import com.alihasanov.courierpay.repository.UserRepository;
import com.alihasanov.courierpay.security.JwtAuthenticationFilter;
import com.alihasanov.courierpay.security.JwtService;
import com.alihasanov.courierpay.service.AuthService;
import com.alihasanov.courierpay.service.BalanceService;
import com.alihasanov.courierpay.service.CompanyService;
import com.alihasanov.courierpay.service.CourierAccessService;
import com.alihasanov.courierpay.service.CourierService;
import com.alihasanov.courierpay.service.CustomUserDetailsService;
import com.alihasanov.courierpay.service.EarningService;
import com.alihasanov.courierpay.service.PayoutService;
import com.alihasanov.courierpay.service.RefreshTokenService;
import com.alihasanov.courierpay.service.ReportService;
import com.alihasanov.courierpay.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class CourierPayApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fullApplicationContext_shouldWireControllersServicesRepositoriesAndSecurity() {
        assertThat(applicationContext).isNotNull();

        assertBeansPresent(
                HealthController.class,
                AuthController.class,
                CompanyController.class,
                CourierController.class,
                EarningController.class,
                BalanceController.class,
                PayoutController.class,
                ReportController.class,

                AuthService.class,
                CompanyService.class,
                CourierService.class,
                CourierAccessService.class,
                EarningService.class,
                BalanceService.class,
                PayoutService.class,
                ReportService.class,
                TransactionService.class,
                RefreshTokenService.class,
                CustomUserDetailsService.class,

                UserRepository.class,
                CompanyRepository.class,
                CourierRepository.class,
                EarningRepository.class,
                BalanceRepository.class,
                PayoutRepository.class,
                TransactionRepository.class,
                RefreshTokenRepository.class,

                SecurityConfig.class,
                ShedLockConfig.class,
                JwtAuthenticationFilter.class,
                JwtService.class
        );
    }

    @Test
    void healthEndpoint_shouldBeAvailableInFullApplicationContext() throws Exception {
        mockMvc.perform(get("/healthz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("courierpay"))
                .andExpect(jsonPath("$.status").value("UP"));
    }

    private void assertBeansPresent(Class<?>... beanTypes) {
        for (Class<?> beanType : beanTypes) {
            assertThat(applicationContext.getBean(beanType))
                    .as("Expected full application context to wire %s", beanType.getSimpleName())
                    .isNotNull();
        }
    }
}
