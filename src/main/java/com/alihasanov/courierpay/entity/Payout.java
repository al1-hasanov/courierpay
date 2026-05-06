package com.alihasanov.courierpay.entity;

import com.alihasanov.courierpay.enums.PayoutStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "payouts")
public class Payout {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "courier_id", nullable = false)
    private Courier courier;
    @Column(nullable = false)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private PayoutStatus status;
    @Column(name = "requested_at")
    private Instant requestedAt;
    @Column(name = "completed_at")
    private Instant completedAt;
    @PrePersist void prePersist() { requestedAt = Instant.now(); }
}
