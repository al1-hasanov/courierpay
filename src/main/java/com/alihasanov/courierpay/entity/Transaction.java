package com.alihasanov.courierpay.entity;

import com.alihasanov.courierpay.enums.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions", uniqueConstraints = {
        @UniqueConstraint(name = "uk_transactions_type_reference_id", columnNames = {"type", "reference_id"})
})
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id", nullable = false)
    private Courier courier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "reference_id")
    private Long referenceId;

    private String description;

    @Column(name = "created_at")
    private Instant createdAt;

    public Transaction() {
    }

    public Transaction(Long id, Courier courier, TransactionType type, BigDecimal amount, Long referenceId, String description, Instant createdAt) {
        this.id = id;
        this.courier = courier;
        this.type = type;
        this.amount = amount;
        this.referenceId = referenceId;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static TransactionBuilder builder() { return new TransactionBuilder(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Courier getCourier() { return courier; }
    public void setCourier(Courier courier) { this.courier = courier; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @PrePersist
    void prePersist() { createdAt = Instant.now(); }

    public static class TransactionBuilder {
        private Long id;
        private Courier courier;
        private TransactionType type;
        private BigDecimal amount;
        private Long referenceId;
        private String description;
        private Instant createdAt;
        public TransactionBuilder id(Long id) { this.id = id; return this; }
        public TransactionBuilder courier(Courier courier) { this.courier = courier; return this; }
        public TransactionBuilder type(TransactionType type) { this.type = type; return this; }
        public TransactionBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public TransactionBuilder referenceId(Long referenceId) { this.referenceId = referenceId; return this; }
        public TransactionBuilder description(String description) { this.description = description; return this; }
        public TransactionBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Transaction build() { return new Transaction(id, courier, type, amount, referenceId, description, createdAt); }
    }
}
