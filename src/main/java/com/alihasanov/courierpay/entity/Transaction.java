package com.alihasanov.courierpay.entity;

import com.alihasanov.courierpay.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "transactions", uniqueConstraints = {
        @UniqueConstraint(name = "uk_transactions_type_reference_id", columnNames = {"type", "reference_id"})
})
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "courier_id", nullable = false)
    private Courier courier;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private TransactionType type;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(name = "reference_id")
    private Long referenceId;
    private String description;
    @Column(name = "created_at")
    private Instant createdAt;
    @PrePersist void prePersist() { createdAt = Instant.now(); }
}
