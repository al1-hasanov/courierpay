package com.alihasanov.courierpay.entity;

import com.alihasanov.courierpay.enums.PayoutStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payouts")
public class Payout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id", nullable = false)
    private Courier courier;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayoutStatus status;

    @Column(name = "requested_at")
    private Instant requestedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    public Payout() {
    }

    public Payout(Long id, Courier courier, BigDecimal amount, PayoutStatus status, Instant requestedAt, Instant completedAt) {
        this.id = id;
        this.courier = courier;
        this.amount = amount;
        this.status = status;
        this.requestedAt = requestedAt;
        this.completedAt = completedAt;
    }

    public static PayoutBuilder builder() { return new PayoutBuilder(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Courier getCourier() { return courier; }
    public void setCourier(Courier courier) { this.courier = courier; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public PayoutStatus getStatus() { return status; }
    public void setStatus(PayoutStatus status) { this.status = status; }
    public Instant getRequestedAt() { return requestedAt; }
    public void setRequestedAt(Instant requestedAt) { this.requestedAt = requestedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    @PrePersist
    void prePersist() { requestedAt = Instant.now(); }

    public static class PayoutBuilder {
        private Long id;
        private Courier courier;
        private BigDecimal amount;
        private PayoutStatus status;
        private Instant requestedAt;
        private Instant completedAt;
        public PayoutBuilder id(Long id) { this.id = id; return this; }
        public PayoutBuilder courier(Courier courier) { this.courier = courier; return this; }
        public PayoutBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public PayoutBuilder status(PayoutStatus status) { this.status = status; return this; }
        public PayoutBuilder requestedAt(Instant requestedAt) { this.requestedAt = requestedAt; return this; }
        public PayoutBuilder completedAt(Instant completedAt) { this.completedAt = completedAt; return this; }
        public Payout build() { return new Payout(id, courier, amount, status, requestedAt, completedAt); }
    }
}
