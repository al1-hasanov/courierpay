package com.alihasanov.courierpay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "balances")
public class Balance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "courier_id", nullable = false)
    private Courier courier;
    @Column(name = "available_amount", nullable = false)
    private BigDecimal availableAmount;
    @Column(name = "reserved_amount", nullable = false)
    private BigDecimal reservedAmount;
    @Version
    private Long version;
    @Column(name = "updated_at")
    private Instant updatedAt;
    @PrePersist void prePersist() { updatedAt = Instant.now(); }
    @PreUpdate void preUpdate() { updatedAt = Instant.now(); }
}
