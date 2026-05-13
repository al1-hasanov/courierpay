package com.alihasanov.courierpay.exception;

import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationExceptionTest {

    @Test
    void getMessage_shouldKeepPlaceholdersWhenNoArgumentsProvided() {
        var exception = new BusinessException("Balance for courier {courierId} is too low");

        assertThat(exception.getMessage())
                .isEqualTo("Balance for courier {courierId} is too low");
    }

    @Test
    void getMessage_shouldReplaceArgumentsFromErrorResponseTemplate() {
        var exception = new BusinessException(
                CourierPayErrorResponse.BUSINESS_ERROR,
                Map.of("message", "Requested payout exceeds available balance")
        );

        assertThat(exception.getMessage())
                .isEqualTo("Requested payout exceeds available balance");
    }

    @Test
    void getLocalizedMessage_shouldFallBackToDefaultMessageWhenMessageSourceIsMissing() {
        var exception = new NotFoundException(CourierPayErrorResponse.COURIER_NOT_FOUND);

        assertThat(exception.getLocalizedMessage(Locale.ENGLISH, null))
                .isEqualTo("Courier not found");
    }
}
