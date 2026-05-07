package com.alihasanov.courierpay.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "commission_rate", nullable = false)
    private BigDecimal commissionRate;

    @Column(name = "created_at")
    private Instant createdAt;

    public Company() {
    }

    public Company(Long id, String name, BigDecimal commissionRate, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.commissionRate = commissionRate;
        this.createdAt = createdAt;
    }

    public static CompanyBuilder builder() { return new CompanyBuilder(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @PrePersist
    void prePersist() { createdAt = Instant.now(); }

    public static class CompanyBuilder {
        private Long id;
        private String name;
        private BigDecimal commissionRate;
        private Instant createdAt;
        public CompanyBuilder id(Long id) { this.id = id; return this; }
        public CompanyBuilder name(String name) { this.name = name; return this; }
        public CompanyBuilder commissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; return this; }
        public CompanyBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Company build() { return new Company(id, name, commissionRate, createdAt); }
    }
}
