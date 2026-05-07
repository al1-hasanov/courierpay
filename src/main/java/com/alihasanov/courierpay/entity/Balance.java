package com.alihasanov.courierpay.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "balances")
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id", nullable = false)
    private Courier courier;

    @Column(name = "available_amount", nullable = false)
    private BigDecimal availableAmount;

    @Column(name = "reserved_amount", nullable = false)
    private BigDecimal reservedAmount;

    @Version
    private Long version;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Balance() {
    }

    public Balance(Long id, Courier courier, BigDecimal availableAmount, BigDecimal reservedAmount, Long version, Instant updatedAt) {
        this.id = id;
        this.courier = courier;
        this.availableAmount = availableAmount;
        this.reservedAmount = reservedAmount;
        this.version = version;
        this.updatedAt = updatedAt;
    }

    public static BalanceBuilder builder() { return new BalanceBuilder(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Courier getCourier() { return courier; }
    public void setCourier(Courier courier) { this.courier = courier; }
    public BigDecimal getAvailableAmount() { return availableAmount; }
    public void setAvailableAmount(BigDecimal availableAmount) { this.availableAmount = availableAmount; }
    public BigDecimal getReservedAmount() { return reservedAmount; }
    public void setReservedAmount(BigDecimal reservedAmount) { this.reservedAmount = reservedAmount; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    void prePersist() { updatedAt = Instant.now(); }

    @PreUpdate
    void preUpdate() { updatedAt = Instant.now(); }

    public static class BalanceBuilder {
        private Long id;
        private Courier courier;
        private BigDecimal availableAmount;
        private BigDecimal reservedAmount;
        private Long version;
        private Instant updatedAt;
        public BalanceBuilder id(Long id) { this.id = id; return this; }
        public BalanceBuilder courier(Courier courier) { this.courier = courier; return this; }
        public BalanceBuilder availableAmount(BigDecimal availableAmount) { this.availableAmount = availableAmount; return this; }
        public BalanceBuilder reservedAmount(BigDecimal reservedAmount) { this.reservedAmount = reservedAmount; return this; }
        public BalanceBuilder version(Long version) { this.version = version; return this; }
        public BalanceBuilder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public Balance build() { return new Balance(id, courier, availableAmount, reservedAmount, version, updatedAt); }
    }
}
