package com.alihasanov.courierpay.entity;

import com.alihasanov.courierpay.enums.EarningStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "earnings")
public class Earning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id", nullable = false)
    private Courier courier;

    @Column(name = "gross_amount", nullable = false)
    private BigDecimal grossAmount;

    @Column(name = "commission_amount", nullable = false)
    private BigDecimal commissionAmount;

    @Column(name = "net_amount", nullable = false)
    private BigDecimal netAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EarningStatus status;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    public Earning() {
    }

    public Earning(Long id, Courier courier, BigDecimal grossAmount, BigDecimal commissionAmount, BigDecimal netAmount,
                   EarningStatus status, String idempotencyKey, LocalDate workDate, Instant createdAt, Instant processedAt) {
        this.id = id;
        this.courier = courier;
        this.grossAmount = grossAmount;
        this.commissionAmount = commissionAmount;
        this.netAmount = netAmount;
        this.status = status;
        this.idempotencyKey = idempotencyKey;
        this.workDate = workDate;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }

    public static EarningBuilder builder() { return new EarningBuilder(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Courier getCourier() { return courier; }
    public void setCourier(Courier courier) { this.courier = courier; }
    public BigDecimal getGrossAmount() { return grossAmount; }
    public void setGrossAmount(BigDecimal grossAmount) { this.grossAmount = grossAmount; }
    public BigDecimal getCommissionAmount() { return commissionAmount; }
    public void setCommissionAmount(BigDecimal commissionAmount) { this.commissionAmount = commissionAmount; }
    public BigDecimal getNetAmount() { return netAmount; }
    public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
    public EarningStatus getStatus() { return status; }
    public void setStatus(EarningStatus status) { this.status = status; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getProcessedAt() { return processedAt; }
    public void setProcessedAt(Instant processedAt) { this.processedAt = processedAt; }

    @PrePersist
    void prePersist() { createdAt = Instant.now(); }

    public static class EarningBuilder {
        private Long id;
        private Courier courier;
        private BigDecimal grossAmount;
        private BigDecimal commissionAmount;
        private BigDecimal netAmount;
        private EarningStatus status;
        private String idempotencyKey;
        private LocalDate workDate;
        private Instant createdAt;
        private Instant processedAt;
        public EarningBuilder id(Long id) { this.id = id; return this; }
        public EarningBuilder courier(Courier courier) { this.courier = courier; return this; }
        public EarningBuilder grossAmount(BigDecimal grossAmount) { this.grossAmount = grossAmount; return this; }
        public EarningBuilder commissionAmount(BigDecimal commissionAmount) { this.commissionAmount = commissionAmount; return this; }
        public EarningBuilder netAmount(BigDecimal netAmount) { this.netAmount = netAmount; return this; }
        public EarningBuilder status(EarningStatus status) { this.status = status; return this; }
        public EarningBuilder idempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; return this; }
        public EarningBuilder workDate(LocalDate workDate) { this.workDate = workDate; return this; }
        public EarningBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public EarningBuilder processedAt(Instant processedAt) { this.processedAt = processedAt; return this; }
        public Earning build() { return new Earning(id, courier, grossAmount, commissionAmount, netAmount, status, idempotencyKey, workDate, createdAt, processedAt); }
    }
}
