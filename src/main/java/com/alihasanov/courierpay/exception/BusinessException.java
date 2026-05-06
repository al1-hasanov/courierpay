package com.alihasanov.courierpay.exception;

import java.util.Map;

public class BusinessException extends ApplicationException {

    public BusinessException(String message) {
        super(CourierPayErrorResponse.BUSINESS_ERROR, Map.of("message", message));
    }

    public BusinessException(ErrorResponse errorResponse) {
        super(errorResponse);
    }

    public BusinessException(ErrorResponse errorResponse, Map<String, Object> messageArguments) {
        super(errorResponse, messageArguments);
    }
}
