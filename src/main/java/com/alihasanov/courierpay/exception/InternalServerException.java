package com.alihasanov.courierpay.exception;

import java.util.Map;

public class InternalServerException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public InternalServerException(String message, Throwable cause) {
        super(CourierPayErrorResponse.INTERNAL_SERVER_ERROR, Map.of("message", message), cause);
    }

    public InternalServerException(ErrorResponse errorResponse) {
        super(errorResponse);
    }

    public InternalServerException(ErrorResponse errorResponse, Throwable cause) {
        super(errorResponse, cause);
    }

    public InternalServerException(ErrorResponse errorResponse, Map<String, Object> messageArguments, Throwable cause) {
        super(errorResponse, messageArguments, cause);
    }
}
