package com.alihasanov.courierpay.exception;

import java.util.Map;

public class NotFoundException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public NotFoundException(String message) {
        super(CourierPayErrorResponse.NOT_FOUND, Map.of("message", message));
    }

    public NotFoundException(ErrorResponse errorResponse) {
        super(errorResponse);
    }

    public NotFoundException(ErrorResponse errorResponse, Map<String, Object> messageArguments) {
        super(errorResponse, messageArguments);
    }
}
