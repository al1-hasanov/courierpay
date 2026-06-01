package com.alihasanov.courierpay.dto;

import com.alihasanov.courierpay.enums.AuditAction;
import com.alihasanov.courierpay.enums.AuditStatus;

import java.time.Instant;

public class AuditLogDtos {
    public record AuditLogResponse(
            Long id,
            Long actorUserId,
            String actorEmail,
            String actorRole,
            AuditAction action,
            String entityType,
            String entityId,
            AuditStatus status,
            String message,
            String metadataJson,
            String ipAddress,
            String userAgent,
            Instant createdAt
    ) {
    }
}
