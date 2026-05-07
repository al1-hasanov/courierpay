package com.alihasanov.courierpay.entity;

import com.alihasanov.courierpay.enums.RoleName;
import com.alihasanov.courierpay.enums.UserStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "users")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleName role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(name = "created_at")
    private Instant createdAt;

    public AppUser() {
    }

    public AppUser(Long id, String email, String passwordHash, String fullName, RoleName role, UserStatus status, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static AppUserBuilder builder() {
        return new AppUserBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public RoleName getRole() { return role; }
    public void setRole(RoleName role) { this.role = role; }
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
        if (status == null) status = UserStatus.ACTIVE;
    }

    public static class AppUserBuilder {
        private Long id;
        private String email;
        private String passwordHash;
        private String fullName;
        private RoleName role;
        private UserStatus status;
        private Instant createdAt;

        public AppUserBuilder id(Long id) { this.id = id; return this; }
        public AppUserBuilder email(String email) { this.email = email; return this; }
        public AppUserBuilder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public AppUserBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public AppUserBuilder role(RoleName role) { this.role = role; return this; }
        public AppUserBuilder status(UserStatus status) { this.status = status; return this; }
        public AppUserBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public AppUser build() { return new AppUser(id, email, passwordHash, fullName, role, status, createdAt); }
    }
}
