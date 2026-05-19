package com.alihasanov.courierpay.config;

import com.alihasanov.courierpay.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static com.alihasanov.courierpay.enums.RoleName.ADMIN;
import static com.alihasanov.courierpay.enums.RoleName.COMPANY_MANAGER;
import static com.alihasanov.courierpay.enums.RoleName.COURIER;

@Configuration
@EnableMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true
)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${app.cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/",
                                "/healthz",
                                "/api/v1/auth/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/v1/companies").hasAuthority(admin())
                        .requestMatchers(HttpMethod.GET, "/api/v1/companies").hasAnyAuthority(adminOrCompanyManager())
                        .requestMatchers(HttpMethod.POST, "/api/v1/couriers").hasAnyAuthority(adminOrCompanyManager())
                        .requestMatchers(HttpMethod.GET, "/api/v1/couriers").hasAnyAuthority(adminOrCompanyManager())
                        .requestMatchers(HttpMethod.POST, "/api/v1/earnings").hasAnyAuthority(adminOrCompanyManager())
                        .requestMatchers(HttpMethod.POST, "/api/v1/earnings/{id}/process").hasAuthority(admin())
                        .requestMatchers(HttpMethod.GET, "/api/v1/earnings").hasAnyAuthority(adminOrCompanyManager())
                        .requestMatchers(HttpMethod.GET, "/api/v1/balances/couriers/{courierId}").hasAnyAuthority(adminCompanyManagerOrCourier())
                        .requestMatchers(HttpMethod.POST, "/api/v1/payouts").hasAuthority(courier())
                        .requestMatchers(HttpMethod.POST, "/api/v1/payouts/{id}/approve").hasAuthority(admin())
                        .requestMatchers(HttpMethod.POST, "/api/v1/payouts/{id}/reject").hasAuthority(admin())
                        .requestMatchers(HttpMethod.GET, "/api/v1/payouts").hasAnyAuthority(adminCompanyManagerOrCourier())
                        .requestMatchers(HttpMethod.GET, "/api/v1/reports/**").hasAnyAuthority(adminOrCompanyManager())
                        .anyRequest().denyAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(parseAllowedOrigins());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Content-Disposition"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private List<String> parseAllowedOrigins() {
        return Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    private String[] adminOrCompanyManager() {
        return List.of(admin(), companyManager()).toArray(String[]::new);
    }

    private String[] adminCompanyManagerOrCourier() {
        return List.of(admin(), companyManager(), courier()).toArray(String[]::new);
    }

    private String admin() {
        return authority(ADMIN.name());
    }

    private String companyManager() {
        return authority(COMPANY_MANAGER.name());
    }

    private String courier() {
        return authority(COURIER.name());
    }

    private String authority(String role) {
        return "ROLE_" + role;
    }
}
