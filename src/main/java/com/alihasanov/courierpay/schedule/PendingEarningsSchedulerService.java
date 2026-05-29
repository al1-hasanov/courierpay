package com.alihasanov.courierpay.schedule;

import com.alihasanov.courierpay.enums.EarningStatus;
import com.alihasanov.courierpay.repository.EarningRepository;
import com.alihasanov.courierpay.service.EarningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PendingEarningsSchedulerService {
    private final EarningRepository earningRepository;
    private final EarningService earningService;
    private final ExecutorService virtualThreadExecutor;

    @Scheduled(cron = "0 */10 * * * *")
    @SchedulerLock(name = "processPendingEarnings", lockAtMostFor = "9m", lockAtLeastFor = "30s")
    public void processPendingEarningsJob() {
        var pendingEarnings = earningRepository.findTop100ByStatusOrderByCreatedAtAsc(EarningStatus.PENDING);

        var futures = pendingEarnings.stream()
                .map(earning -> CompletableFuture.runAsync(() -> {
                    log.info("Processing earning {} on thread {}. Virtual thread: {}",
                            earning.getId(), Thread.currentThread().getName(), Thread.currentThread().isVirtual());
                    earningService.process(earning.getId());
                }, virtualThreadExecutor))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();

        log.info("Processed {} pending earnings", pendingEarnings.size());
    }
}
