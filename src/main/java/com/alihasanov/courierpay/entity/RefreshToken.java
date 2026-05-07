package com.alihasanov.courierpay.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public RefreshToken() {
    }

    public RefreshToken(Long id, String token, AppUser user, Instant expiresAt, boolean revoked, Instant createdAt) {
        this.id = id;
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.createdAt = createdAt;
    }

    public static RefreshTokenBuilder builder() { return new RefreshTokenBuilder(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @PrePersist
    void prePersist() { createdAt = Instant.now(); }

    public static class RefreshTokenBuilder {
        private Long id;
        private String token;
        private AppUser user;
        private Instant expiresAt;
        private boolean revoked;
        private Instant createdAt;
        public RefreshTokenBuilder id(Long id) { this.id = id; return this; }
        public RefreshTokenBuilder token(String token) { this.token = token; return this; }
        public RefreshTokenBuilder user(AppUser user) { this.user = user; return this; }
        public RefreshTokenBuilder expiresAt(Instant expiresAt) { this.expiresAt = expiresAt; return this; }
        public RefreshTokenBuilder revoked(boolean revoked) { this.revoked = revoked; return this; }
        public RefreshTokenBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public RefreshToken build() { return new RefreshToken(id, token, user, expiresAt, revoked, createdAt); }
    }
}
