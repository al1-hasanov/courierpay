package com.alihasanov.courierpay.entity;

import com.alihasanov.courierpay.enums.AuditAction;
import com.alihasanov.courierpay.enums.AuditStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actor_user_id")
    private Long actorUserId;

    @Column(name = "actor_email")
    private String actorEmail;

    @Column(name = "actor_role")
    private String actorRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "entity_id")
    private String entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditStatus status;

    @Column(length = 500)
    private String message;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "created_at")
    private Instant createdAt;

    public AuditLog() {
    }

    public AuditLog(Long id, Long actorUserId, String actorEmail, String actorRole, AuditAction action, String entityType, String entityId, AuditStatus status, String message, String metadataJson, String ipAddress, String userAgent, Instant createdAt) {
        this.id = id;
        this.actorUserId = actorUserId;
        this.actorEmail = actorEmail;
        this.actorRole = actorRole;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.status = status;
        this.message = message;
        this.metadataJson = metadataJson;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = createdAt;
    }

    public static AuditLogBuilder builder() {
        return new AuditLogBuilder();
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getActorUserId() { return actorUserId; }
    public void setActorUserId(Long actorUserId) { this.actorUserId = actorUserId; }
    public String getActorEmail() { return actorEmail; }
    public void setActorEmail(String actorEmail) { this.actorEmail = actorEmail; }
    public String getActorRole() { return actorRole; }
    public void setActorRole(String actorRole) { this.actorRole = actorRole; }
    public AuditAction getAction() { return action; }
    public void setAction(AuditAction action) { this.action = action; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public AuditStatus getStatus() { return status; }
    public void setStatus(AuditStatus status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getMetadataJson() { return metadataJson; }
    public void setMetadataJson(String metadataJson) { this.metadataJson = metadataJson; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static class AuditLogBuilder {
        private Long id;
        private Long actorUserId;
        private String actorEmail;
        private String actorRole;
        private AuditAction action;
        private String entityType;
        private String entityId;
        private AuditStatus status;
        private String message;
        private String metadataJson;
        private String ipAddress;
        private String userAgent;
        private Instant createdAt;

        public AuditLogBuilder id(Long id) { this.id = id; return this; }
        public AuditLogBuilder actorUserId(Long actorUserId) { this.actorUserId = actorUserId; return this; }
        public AuditLogBuilder actorEmail(String actorEmail) { this.actorEmail = actorEmail; return this; }
        public AuditLogBuilder actorRole(String actorRole) { this.actorRole = actorRole; return this; }
        public AuditLogBuilder action(AuditAction action) { this.action = action; return this; }
        public AuditLogBuilder entityType(String entityType) { this.entityType = entityType; return this; }
        public AuditLogBuilder entityId(String entityId) { this.entityId = entityId; return this; }
        public AuditLogBuilder status(AuditStatus status) { this.status = status; return this; }
        public AuditLogBuilder message(String message) { this.message = message; return this; }
        public AuditLogBuilder metadataJson(String metadataJson) { this.metadataJson = metadataJson; return this; }
        public AuditLogBuilder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public AuditLogBuilder userAgent(String userAgent) { this.userAgent = userAgent; return this; }
        public AuditLogBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public AuditLog build() {
            return new AuditLog(id, actorUserId, actorEmail, actorRole, action, entityType, entityId, status, message, metadataJson, ipAddress, userAgent, createdAt);
        }
    }
}
