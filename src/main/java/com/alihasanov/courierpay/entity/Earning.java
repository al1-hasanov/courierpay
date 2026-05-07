package com.alihasanov.courierpay.entity;

import com.alihasanov.courierpay.enums.EarningStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "earnings")
public class Earning {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "courier_id", nullable = false)
    private Courier courier;
    @Column(name = "gross_amount", nullable = false)
    private BigDecimal grossAmount;
    @Column(name = "commission_amount", nullable = false)
    private BigDecimal commissionAmount;
    @Column(name = "net_amount", nullable = false)
    private BigDecimal netAmount;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private EarningStatus status;
    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;
    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "processed_at")
    private Instant processedAt;

    public Long getId() {
        return id;
    }
    @PrePersist void prePersist() { createdAt = Instant.now(); }
}
