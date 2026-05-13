package com.alihasanov.courierpay.service;

import com.alihasanov.courierpay.event.EarningCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class EarningCreatedEventListenerService {
    private final EarningService earningService;

    @KafkaListener(topics = "${app.kafka.topics.earning-created}", groupId = "courierpay-earning-processor")
    public void onEarningCreated(EarningCreatedEvent event) {
        earningService.process(event.earningId());
    }
}
