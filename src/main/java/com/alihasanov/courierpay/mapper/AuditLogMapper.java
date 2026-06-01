package com.alihasanov.courierpay.mapper;

import com.alihasanov.courierpay.dto.AuditLogDtos.AuditLogResponse;
import com.alihasanov.courierpay.entity.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {
    public AuditLogResponse toResponse(AuditLog auditLog) {
        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getActorUserId(),
                auditLog.getActorEmail(),
                auditLog.getActorRole(),
                auditLog.getAction(),
                auditLog.getEntityType(),
                auditLog.getEntityId(),
                auditLog.getStatus(),
                auditLog.getMessage(),
                auditLog.getMetadataJson(),
                auditLog.getIpAddress(),
                auditLog.getUserAgent(),
                auditLog.getCreatedAt()
        );
    }
}
