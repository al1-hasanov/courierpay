package com.alihasanov.courierpay.repository;

import com.alihasanov.courierpay.entity.AppUser;
import com.alihasanov.courierpay.enums.RoleName;
import com.alihasanov.courierpay.enums.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.liquibase.enabled=false")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_shouldReturnMatchingUser() {
        var user = AppUser.builder()
                .email("courier@example.com")
                .passwordHash("encoded-password")
                .fullName("Ali Hasanov")
                .role(RoleName.COURIER)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.saveAndFlush(user);

        var result = userRepository.findByEmail("courier@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getFullName()).isEqualTo("Ali Hasanov");
        assertThat(result.get().getRole()).isEqualTo(RoleName.COURIER);
    }

    @Test
    void existsByEmail_shouldReturnFalseWhenEmailDoesNotExist() {
        assertThat(userRepository.existsByEmail("missing@example.com")).isFalse();
    }
}
