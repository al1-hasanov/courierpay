package com.alihasanov.courierpay.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "couriers")
public class Courier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "phone_number")
    private String phoneNumber;

    private boolean active;

    @Column(name = "created_at")
    private Instant createdAt;

    public Courier() {
    }

    public Courier(Long id, AppUser user, Company company, String phoneNumber, boolean active, Instant createdAt) {
        this.id = id;
        this.user = user;
        this.company = company;
        this.phoneNumber = phoneNumber;
        this.active = active;
        this.createdAt = createdAt;
    }

    public static CourierBuilder builder() { return new CourierBuilder(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @PrePersist
    void prePersist() { createdAt = Instant.now(); active = true; }

    public static class CourierBuilder {
        private Long id;
        private AppUser user;
        private Company company;
        private String phoneNumber;
        private boolean active;
        private Instant createdAt;
        public CourierBuilder id(Long id) { this.id = id; return this; }
        public CourierBuilder user(AppUser user) { this.user = user; return this; }
        public CourierBuilder company(Company company) { this.company = company; return this; }
        public CourierBuilder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public CourierBuilder active(boolean active) { this.active = active; return this; }
        public CourierBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Courier build() { return new Courier(id, user, company, phoneNumber, active, createdAt); }
    }
}
