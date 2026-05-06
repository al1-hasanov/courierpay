package com.alihasanov.courierpay.exception;

public record ConstraintsViolationError(String property, String message) {
}
