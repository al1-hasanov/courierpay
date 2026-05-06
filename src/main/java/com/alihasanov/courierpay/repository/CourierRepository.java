package com.alihasanov.courierpay.repository;

import com.alihasanov.courierpay.entity.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourierRepository extends JpaRepository<Courier, Long> {}
