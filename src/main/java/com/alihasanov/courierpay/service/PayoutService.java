package com.alihasanov.courierpay.service;

import com.alihasanov.courierpay.enums.AuditAction;
import com.alihasanov.courierpay.repository.PayoutRepository;
import com.alihasanov.courierpay.exception.BusinessException;
import com.alihasanov.courierpay.exception.NotFoundException;
import com.alihasanov.courierpay.enums.PayoutStatus;
import com.alihasanov.courierpay.enums.TransactionType;
import com.alihasanov.courierpay.entity.Payout;
import com.alihasanov.courierpay.event.PayoutRequestedEvent;
import com.alihasanov.courierpay.mapper.PayoutMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.alihasanov.courierpay.dto.PayoutDtos.*;
import static com.alihasanov.courierpay.exception.CourierPayErrorResponse.ONLY_REQUESTED_PAYOUTS_CAN_BE_APPROVED;
import static com.alihasanov.courierpay.exception.CourierPayErrorResponse.PAYOUT_NOT_FOUND;
import static com.alihasanov.courierpay.exception.CourierPayErrorResponse.REQUESTED_PAYOUT_EXCEEDS_AVAILABLE_BALANCE;

@Service
@RequiredArgsConstructor
public class PayoutService {
    private final PayoutRepository payoutRepository;
    private final CourierService courierService;
    private final BalanceService balanceService;
    private final TransactionService transactionService;
    private final CourierAccessService courierAccessService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PayoutMapper payoutMapper;
    private final AuditLogService auditLogService;

    @Value("${app.kafka.enabled:false}")
    private boolean kafkaEnabled;

    @Value("${app.kafka.topics.payout-requested}")
    private String payoutRequestedTopic;

    @Transactional
    public PayoutResponse request(RequestPayoutRequest request) {
        courierAccessService.assertCanAccessCourier(request.courierId());
        var courier = courierService.get(request.courierId());
        var balance = balanceService.getByCourierId(courier.getId());
        if (balance.getAvailableAmount().compareTo(request.amount()) < 0) {
            throw new BusinessException(REQUESTED_PAYOUT_EXCEEDS_AVAILABLE_BALANCE);
        }
        var payout = payoutRepository.save(Payout.builder()
                .courier(courier)
                .amount(request.amount())
                .status(PayoutStatus.REQUESTED)
                .build());
        if (kafkaEnabled) {
            kafkaTemplate.send(payoutRequestedTopic, payout.getId().toString(), new PayoutRequestedEvent(payout.getId()));
        }
        auditLogService.success(
                AuditAction.REQUESTED_PAYOUT,
                "Payout",
                payout.getId(),
                "Payout requested",
                "{\"courierId\":" + courier.getId()
                        + ",\"amount\":" + payout.getAmount()
                        + ",\"status\":\"" + payout.getStatus().name() + "\"}"
        );
        return payoutMapper.toResponse(payout);
    }

    @Transactional
    public PayoutResponse approve(Long payoutId) {
        var payout = get(payoutId);
        if (payout.getStatus() != PayoutStatus.REQUESTED) {
            throw new BusinessException(ONLY_REQUESTED_PAYOUTS_CAN_BE_APPROVED);
        }
        balanceService.debit(payout.getCourier().getId(), payout.getAmount());
        transactionService.record(payout.getCourier(), TransactionType.PAYOUT_DEBIT, payout.getAmount(), payout.getId(), "Payout completed internally");
        payout.setStatus(PayoutStatus.COMPLETED);
        payout.setCompletedAt(Instant.now());
        auditLogService.success(
                AuditAction.APPROVED_PAYOUT,
                "Payout",
                payout.getId(),
                "Payout approved",
                "{\"courierId\":" + payout.getCourier().getId() + ",\"amount\":" + payout.getAmount() + "}"
        );
        return payoutMapper.toResponse(payout);
    }

    @Transactional
    public PayoutResponse reject(Long payoutId) {
        var payout = get(payoutId);
        payout.setStatus(PayoutStatus.REJECTED);
        auditLogService.success(
                AuditAction.REJECTED_PAYOUT,
                "Payout",
                payout.getId(),
                "Payout rejected",
                "{\"courierId\":" + payout.getCourier().getId() + ",\"amount\":" + payout.getAmount() + "}"
        );
        return payoutMapper.toResponse(payout);
    }

    @Transactional(readOnly = true)
    public Page<PayoutResponse> findAll(Long courierId, PayoutStatus status, Instant requestedFrom, Instant requestedTo, Pageable pageable) {
        Long effectiveCourierId = courierAccessService.isCurrentUserCourier()
                ? courierAccessService.getCurrentCourierId()
                : courierId;
        return payoutRepository.findAll(buildSpecification(effectiveCourierId, status, requestedFrom, requestedTo), pageable)
                .map(payoutMapper::toResponse);
    }

    private Specification<Payout> buildSpecification(Long courierId, PayoutStatus status, Instant requestedFrom, Instant requestedTo) {
        return (root, query, criteriaBuilder) -> {
            var predicate = criteriaBuilder.conjunction();
            if (courierId != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("courier").get("id"), courierId));
            }
            if (status != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), status));
            }
            if (requestedFrom != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.<Instant>get("requestedAt"), requestedFrom));
            }
            if (requestedTo != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.<Instant>get("requestedAt"), requestedTo));
            }
            return predicate;
        };
    }

    private Payout get(Long id) { return payoutRepository.findById(id).orElseThrow(() -> new NotFoundException(PAYOUT_NOT_FOUND)); }
}
