package com.alihasanov.courierpay.service;

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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

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
        return payoutMapper.toResponse(payout);
    }

    @Transactional
    public PayoutResponse reject(Long payoutId) {
        var payout = get(payoutId);
        payout.setStatus(PayoutStatus.REJECTED);
        return payoutMapper.toResponse(payout);
    }

    public List<PayoutResponse> findAll() {
        if (courierAccessService.isCurrentUserCourier()) {
            var courierId = courierAccessService.getCurrentCourierId();
            return payoutRepository.findByCourierId(courierId).stream().map(payoutMapper::toResponse).toList();
        }
        return payoutRepository.findAll().stream().map(payoutMapper::toResponse).toList();
    }

    private Payout get(Long id) { return payoutRepository.findById(id).orElseThrow(() -> new NotFoundException(PAYOUT_NOT_FOUND)); }
}
