package com.alihasanov.courierpay.schedule;

import com.alihasanov.courierpay.enums.EarningStatus;
import com.alihasanov.courierpay.repository.EarningRepository;
import com.alihasanov.courierpay.service.EarningService;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PendingEarningsSchedulerService {
    private final EarningRepository earningRepository;
    private final EarningService earningService;

    @Scheduled(cron = "0 */10 * * * *")
    @SchedulerLock(name = "processPendingEarnings", lockAtMostFor = "9m", lockAtLeastFor = "30s")
    public void processPendingEarningsJob() {
        earningRepository.findTop100ByStatusOrderByCreatedAtAsc(EarningStatus.PENDING)
                .forEach(earning -> earningService.process(earning.getId()));
    }
}
