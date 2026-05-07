package com.alihasanov.courierpay.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class KafkaTopicConfig {
    @Bean
    NewTopic earningCreatedTopic(@Value("${app.kafka.topics.earning-created}") String topic) {
        return new NewTopic(topic, 1, (short) 1);
    }

    @Bean
    NewTopic payoutRequestedTopic(@Value("${app.kafka.topics.payout-requested}") String topic) {
        return new NewTopic(topic, 1, (short) 1);
    }
}
