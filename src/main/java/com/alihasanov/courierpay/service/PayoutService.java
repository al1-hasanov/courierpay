package com.alihasanov.courierpay.service;

import com.alihasanov.courierpay.repository.PayoutRepository;
import com.alihasanov.courierpay.exception.BusinessException;
import com.alihasanov.courierpay.exception.NotFoundException;
import com.alihasanov.courierpay.enums.PayoutStatus;
import com.alihasanov.courierpay.enums.TransactionType;
import com.alihasanov.courierpay.entity.Payout;
import com.alihasanov.courierpay.event.PayoutRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static com.alihasanov.courierpay.dto.PayoutDtos.*;

@Service
@RequiredArgsConstructor
public class PayoutService {
    private final PayoutRepository payoutRepository;
    private final CourierService courierService;
    private final BalanceService balanceService;
    private final TransactionService transactionService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.payout-requested}")
    private String payoutRequestedTopic;

    @Transactional
    public PayoutResponse request(RequestPayoutRequest request) {
        var courier = courierService.get(request.courierId());
        var balance = balanceService.getByCourierId(courier.getId());
        if (balance.getAvailableAmount().compareTo(request.amount()) < 0) {
            throw new BusinessException("Requested payout exceeds available balance");
        }
        var payout = payoutRepository.save(Payout.builder()
                .courier(courier)
                .amount(request.amount())
                .status(PayoutStatus.REQUESTED)
                .build());
        kafkaTemplate.send(payoutRequestedTopic, payout.getId().toString(), new PayoutRequestedEvent(payout.getId()));
        return toResponse(payout);
    }

    @Transactional
    public PayoutResponse approve(Long payoutId) {
        var payout = get(payoutId);
        if (payout.getStatus() != PayoutStatus.REQUESTED) {
            throw new BusinessException("Only requested payouts can be approved");
        }
        balanceService.debit(payout.getCourier().getId(), payout.getAmount());
        transactionService.record(payout.getCourier(), TransactionType.PAYOUT_DEBIT, payout.getAmount(), payout.getId(), "Payout completed internally");
        payout.setStatus(PayoutStatus.COMPLETED);
        payout.setCompletedAt(Instant.now());
        return toResponse(payout);
    }

    @Transactional
    public PayoutResponse reject(Long payoutId) {
        var payout = get(payoutId);
        payout.setStatus(PayoutStatus.REJECTED);
        return toResponse(payout);
    }

    public List<PayoutResponse> findAll() { return payoutRepository.findAll().stream().map(this::toResponse).toList(); }

    private Payout get(Long id) { return payoutRepository.findById(id).orElseThrow(() -> new NotFoundException("Payout not found")); }
    private PayoutResponse toResponse(Payout p) { return new PayoutResponse(p.getId(), p.getCourier().getId(), p.getAmount(), p.getStatus()); }
}
