package com.alihasanov.courierpay.controller;

import com.alihasanov.courierpay.dto.AuditLogDtos.AuditLogResponse;
import com.alihasanov.courierpay.enums.AuditAction;
import com.alihasanov.courierpay.enums.AuditStatus;
import com.alihasanov.courierpay.mapper.AuditLogMapper;
import com.alihasanov.courierpay.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {
    private final AuditLogService auditLogService;
    private final AuditLogMapper auditLogMapper;

    @GetMapping
    Page<AuditLogResponse> findAll(
            @RequestParam(required = false) String actorEmail,
            @RequestParam(required = false) AuditAction action,
            @RequestParam(required = false) AuditStatus status,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String entityId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdTo,
            @PageableDefault(size = 50, sort = "id") Pageable pageable
    ) {
        return auditLogService.findAll(actorEmail, action, status, entityType, entityId, createdFrom, createdTo, pageable)
                .map(auditLogMapper::toResponse);
    }
}
