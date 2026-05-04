package com.alihasanov.courierpay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "companies")
public class Company {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(name = "commission_rate", nullable = false)
    private BigDecimal commissionRate;
    @Column(name = "created_at")
    private Instant createdAt;
    @PrePersist void prePersist() { createdAt = Instant.now(); }
}
