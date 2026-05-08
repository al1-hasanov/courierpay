package com.alihasanov.courierpay.repository;

import com.alihasanov.courierpay.entity.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourierRepository extends JpaRepository<Courier, Long> {
    Optional<Courier> findByUserEmail(String email);
}
