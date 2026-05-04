package com.alihasanov.courierpay.service;

import com.alihasanov.courierpay.repository.EarningRepository;
import com.alihasanov.courierpay.exception.BusinessException;
import com.alihasanov.courierpay.enums.EarningStatus;
import com.alihasanov.courierpay.exception.NotFoundException;
import com.alihasanov.courierpay.enums.TransactionType;
import com.alihasanov.courierpay.entity.Earning;
import com.alihasanov.courierpay.event.EarningCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

import static com.alihasanov.courierpay.dto.EarningDtos.*;

@Service
@RequiredArgsConstructor
public class EarningService {
    private final EarningRepository earningRepository;
    private final CourierService courierService;
    private final BalanceService balanceService;
    private final TransactionService transactionService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.earning-created}")
    private String earningCreatedTopic;

    @Transactional
    public EarningResponse create(CreateEarningRequest request) {
        if (earningRepository.existsByIdempotencyKey(request.idempotencyKey())) {
            throw new BusinessException("Duplicate earning idempotency key");
        }
        var courier = courierService.get(request.courierId());
        BigDecimal commissionAmount = request.grossAmount()
                .multiply(courier.getCompany().getCommissionRate())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal netAmount = request.grossAmount().subtract(commissionAmount);

        var earning = earningRepository.save(Earning.builder()
                .courier(courier)
                .grossAmount(request.grossAmount())
                .commissionAmount(commissionAmount)
                .netAmount(netAmount)
                .status(EarningStatus.PENDING)
                .idempotencyKey(request.idempotencyKey())
                .workDate(request.workDate())
                .build());

        kafkaTemplate.send(earningCreatedTopic, earning.getId().toString(), new EarningCreatedEvent(earning.getId()));
        return toResponse(earning);
    }

    @Transactional
    public void process(Long earningId) {
        var earning = earningRepository.findById(earningId).orElseThrow(() -> new NotFoundException("Earning not found"));
        if (earning.getStatus() != EarningStatus.PENDING) return;
        balanceService.credit(earning.getCourier().getId(), earning.getNetAmount());
        transactionService.record(earning.getCourier(), TransactionType.EARNING_CREDIT, earning.getNetAmount(), earning.getId(), "Courier net earning credited");
        transactionService.record(earning.getCourier(), TransactionType.COMMISSION_DEBIT, earning.getCommissionAmount(), earning.getId(), "Company commission calculated");
        earning.setStatus(EarningStatus.PROCESSED);
        earning.setProcessedAt(Instant.now());
    }

    @KafkaListener(topics = "${app.kafka.topics.earning-created}", groupId = "courierpay-earning-processor")
    public void onEarningCreated(EarningCreatedEvent event) {
        process(event.earningId());
    }

    @Scheduled(cron = "0 */10 * * * *")
    @SchedulerLock(name = "processPendingEarnings", lockAtMostFor = "9m", lockAtLeastFor = "30s")
    public void processPendingEarningsJob() {
        earningRepository.findTop100ByStatusOrderByCreatedAtAsc(EarningStatus.PENDING)
                .forEach(e -> process(e.getId()));
    }

    public List<EarningResponse> findAll() {
        return earningRepository.findAll().stream().map(this::toResponse).toList();
    }

    private EarningResponse toResponse(Earning e) {
        return new EarningResponse(e.getId(), e.getCourier().getId(), e.getGrossAmount(), e.getCommissionAmount(), e.getNetAmount(), e.getStatus(), e.getWorkDate());
    }
}
