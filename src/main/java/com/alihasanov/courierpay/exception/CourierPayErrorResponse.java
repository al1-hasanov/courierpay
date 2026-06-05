package com.alihasanov.courierpay.exception;

import org.springframework.http.HttpStatus;

public enum CourierPayErrorResponse implements ErrorResponse {

    BUSINESS_ERROR("BUSINESS_ERROR", HttpStatus.BAD_REQUEST, "{message}"),
    NOT_FOUND("NOT_FOUND", HttpStatus.NOT_FOUND, "{message}"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "{message}"),

    EMAIL_ALREADY_REGISTERED("EMAIL_ALREADY_REGISTERED", HttpStatus.BAD_REQUEST, "Email already registered"),
    COURIER_ACCESS_DENIED("COURIER_ACCESS_DENIED", HttpStatus.FORBIDDEN, "Couriers can only access their own resources"),
    AUTHENTICATION_REQUIRED("AUTHENTICATION_REQUIRED", HttpStatus.FORBIDDEN, "Authentication is required"),
    BALANCE_NOT_FOUND("BALANCE_NOT_FOUND", HttpStatus.NOT_FOUND, "Balance not found"),
    INSUFFICIENT_AVAILABLE_BALANCE("INSUFFICIENT_AVAILABLE_BALANCE", HttpStatus.BAD_REQUEST, "Insufficient available balance"),
    INSUFFICIENT_RESERVED_BALANCE("INSUFFICIENT_RESERVED_BALANCE", HttpStatus.BAD_REQUEST, "Insufficient reserved balance"),
    COMPANY_NOT_FOUND("COMPANY_NOT_FOUND", HttpStatus.NOT_FOUND, "Company not found"),
    USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "User not found"),
    COURIER_NOT_FOUND("COURIER_NOT_FOUND", HttpStatus.NOT_FOUND, "Courier not found"),
    DUPLICATE_EARNING_IDEMPOTENCY_KEY("DUPLICATE_EARNING_IDEMPOTENCY_KEY", HttpStatus.BAD_REQUEST, "Duplicate earning idempotency key"),
    EARNING_NOT_FOUND("EARNING_NOT_FOUND", HttpStatus.NOT_FOUND, "Earning not found"),
    REQUESTED_PAYOUT_EXCEEDS_AVAILABLE_BALANCE("REQUESTED_PAYOUT_EXCEEDS_AVAILABLE_BALANCE", HttpStatus.BAD_REQUEST, "Requested payout exceeds available balance"),
    ONLY_REQUESTED_PAYOUTS_CAN_BE_APPROVED("ONLY_REQUESTED_PAYOUTS_CAN_BE_APPROVED", HttpStatus.BAD_REQUEST, "Only requested payouts can be approved"),
    ONLY_REQUESTED_PAYOUTS_CAN_BE_REJECTED("ONLY_REQUESTED_PAYOUTS_CAN_BE_REJECTED", HttpStatus.BAD_REQUEST, "Only requested payouts can be rejected"),
    PAYOUT_NOT_FOUND("PAYOUT_NOT_FOUND", HttpStatus.NOT_FOUND, "Payout not found"),
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN", HttpStatus.UNAUTHORIZED, "Invalid refresh token"),
    EXPORT_TRANSACTIONS_FAILED("EXPORT_TRANSACTIONS_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "Failed to export transactions");

    private final String key;
    private final HttpStatus httpStatus;
    private final String message;

    CourierPayErrorResponse(String key, HttpStatus httpStatus, String message) {
        this.key = key;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
