package com.alihasanov.courierpay.service;

import com.alihasanov.courierpay.entity.AuditLog;
import com.alihasanov.courierpay.enums.AuditAction;
import com.alihasanov.courierpay.enums.AuditStatus;
import com.alihasanov.courierpay.repository.AuditLogRepository;
import com.alihasanov.courierpay.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void success(AuditAction action, String entityType, Object entityId, String message, String metadataJson) {
        save(action, entityType, entityId, AuditStatus.SUCCESS, message, metadataJson);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failure(AuditAction action, String entityType, Object entityId, String message, String metadataJson) {
        save(action, entityType, entityId, AuditStatus.FAILURE, message, metadataJson);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findAll(String actorEmail,
                                  AuditAction action,
                                  AuditStatus status,
                                  String entityType,
                                  String entityId,
                                  Instant createdFrom,
                                  Instant createdTo,
                                  Pageable pageable) {
        return auditLogRepository.findAll(
                buildSpecification(actorEmail, action, status, entityType, entityId, createdFrom, createdTo),
                pageable
        );
    }

    private void save(AuditAction action,
                      String entityType,
                      Object entityId,
                      AuditStatus status,
                      String message,
                      String metadataJson) {
        var actorEmail = getCurrentActorEmail();
        var actorUser = actorEmail == null ? null : userRepository.findByEmail(actorEmail).orElse(null);
        var request = getCurrentRequest();

        auditLogRepository.save(AuditLog.builder()
                .actorUserId(actorUser == null ? null : actorUser.getId())
                .actorEmail(actorEmail)
                .actorRole(actorUser == null ? null : actorUser.getRole().name())
                .action(action)
                .entityType(entityType)
                .entityId(entityId == null ? null : entityId.toString())
                .status(status)
                .message(message)
                .metadataJson(metadataJson)
                .ipAddress(request == null ? null : getClientIp(request))
                .userAgent(request == null ? null : request.getHeader("User-Agent"))
                .build());
    }

    private Specification<AuditLog> buildSpecification(String actorEmail,
                                                       AuditAction action,
                                                       AuditStatus status,
                                                       String entityType,
                                                       String entityId,
                                                       Instant createdFrom,
                                                       Instant createdTo) {
        return (root, query, criteriaBuilder) -> {
            var predicate = criteriaBuilder.conjunction();

            if (actorEmail != null && !actorEmail.isBlank()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("actorEmail")),
                        "%" + actorEmail.trim().toLowerCase() + "%"
                ));
            }
            if (action != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("action"), action));
            }
            if (status != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), status));
            }
            if (entityType != null && !entityType.isBlank()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("entityType"), entityType.trim()));
            }
            if (entityId != null && !entityId.isBlank()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("entityId"), entityId.trim()));
            }
            if (createdFrom != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), createdFrom));
            }
            if (createdTo != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), createdTo));
            }

            return predicate;
        };
    }

    private String getCurrentActorEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || "anonymousUser".equals(authentication.getName())) {
            return null;
        }
        return authentication.getName();
    }

    private HttpServletRequest getCurrentRequest() {
        var attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

    private String getClientIp(HttpServletRequest request) {
        var forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
