package com.alihasanov.courierpay.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends DefaultErrorAttributes {

    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";
    private static final String ERRORS = "errors";
    private static final String KEY = "key";
    private static final String PATH = "path";

    private final MessageSource messageSource;

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Map<String, Object>> handle(ApplicationException ex, WebRequest request) {
        log.trace("Application exception occurred", ex);

        if (ex.getErrorResponse() == null) {
            log.warn("Error response in ApplicationException is null");
            return ofType(request, HttpStatus.INTERNAL_SERVER_ERROR, ex);
        }

        return ofType(request, ex.getErrorResponse().getHttpStatus(), ex);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Map<String, Object>> handle(MissingRequestHeaderException ex, WebRequest request) {
        log.trace("Required header is missing", ex);
        return ofType(request, HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handle(AccessDeniedException ex, WebRequest request) {
        log.trace("Access to the given resource is denied", ex);
        return ofType(request, HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handle(ConstraintViolationException ex, WebRequest request) {
        log.trace("Constraint violation", ex);
        List<ConstraintsViolationError> validationErrors = ex.getConstraintViolations()
                .stream()
                .map(violation -> new ConstraintsViolationError(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()))
                .collect(Collectors.toList());
        return ofType(request, HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), validationErrors, ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handle(HttpMessageNotReadableException ex, WebRequest request) {
        log.trace("Cannot read HTTP message", ex);

        Throwable root = ex.getRootCause();
        if (root instanceof InvalidFormatException invalidFormatException
                && invalidFormatException.getTargetType() == LocalDate.class) {
            return ofType(
                    request,
                    HttpStatus.BAD_REQUEST,
                    getMessageOrFallback("date.format.invalid", "Invalid date format! (yyyy-MM-dd)"),
                    Collections.emptyList(),
                    "date.format.invalid");
        }

        return ofType(request, HttpStatus.BAD_REQUEST, getLocalizedMessage(ex), Collections.emptyList(), ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handle(MaxUploadSizeExceededException ex, WebRequest request) {
        log.trace("Max upload size exceeded", ex);
        return ofType(request, HttpStatus.PAYLOAD_TOO_LARGE, ex);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handle(MethodArgumentTypeMismatchException ex, WebRequest request) {
        log.trace("Method arguments are not valid", ex);
        return ofType(request, HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(MismatchedInputException.class)
    public ResponseEntity<Map<String, Object>> handle(MismatchedInputException ex, WebRequest request) {
        log.trace("Mismatched input", ex);
        return ofType(request, HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> handle(BindException ex, WebRequest request) {
        List<ConstraintsViolationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ConstraintsViolationError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        return ofType(request, HttpStatus.BAD_REQUEST, getLocalizedMessage(ex), validationErrors,
                ex.getClass().getName() + ".message");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handle(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Method argument not valid", ex);
        List<ConstraintsViolationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ConstraintsViolationError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        return ofType(request, HttpStatus.BAD_REQUEST, getLocalizedMessage(ex), validationErrors, ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handle(BadCredentialsException ex, WebRequest request) {
        log.error("Bad credentials");
        return ofType(request, HttpStatus.UNAUTHORIZED, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handle(Exception ex, WebRequest request) {
        log.error("Server failure", ex);
        return ofType(request, HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    protected ResponseEntity<Map<String, Object>> ofType(WebRequest request,
                                                         HttpStatus status,
                                                         ApplicationException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        return ofType(request, status, ex.getLocalizedMessage(locale, messageSource), Collections.emptyList(),
                ex.getErrorResponse().getKey());
    }

    protected ResponseEntity<Map<String, Object>> ofType(WebRequest request, HttpStatus status, Exception ex) {
        return ofType(request, status, getLocalizedMessage(ex), Collections.emptyList(), ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> ofType(WebRequest request,
                                                       HttpStatus status,
                                                       String message,
                                                       List<?> validationErrors,
                                                       String key) {
        Map<String, Object> attributes = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        attributes.put(STATUS, status.value());
        attributes.put(ERROR, getLocalizedReasonPhrase(status));
        attributes.put(MESSAGE, message);
        attributes.put(ERRORS, validationErrors);
        attributes.put(KEY, key);

        if (request instanceof ServletWebRequest servletWebRequest) {
            attributes.put(PATH, servletWebRequest.getRequest().getRequestURI());
        } else {
            log.warn("Request is not an instance of ServletWebRequest");
        }

        return new ResponseEntity<>(attributes, status);
    }

    private String getMessageOrFallback(String key, String fallback) {
        try {
            return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException ignore) {
            return fallback;
        }
    }

    private String getLocalizedMessage(Exception ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String key = ex.getClass().getName() + ".message";
        if (messageSource != null) {
            try {
                return messageSource.getMessage(key, new Object[]{}, locale);
            } catch (NoSuchMessageException exception) {
                log.warn("Please consider adding localized message for key {} and locale {}", key, locale, exception);
            }
        } else {
            log.warn("Message source is not initialized.");
        }
        return ex.getMessage();
    }

    private String getLocalizedReasonPhrase(HttpStatus status) {
        Locale locale = LocaleContextHolder.getLocale();
        if (messageSource != null) {
            try {
                return messageSource.getMessage(status.value() + ".message", new Object[]{}, locale);
            } catch (NoSuchMessageException exception) {
                log.warn("Please consider adding localized message for key {} and locale {}", status.value(), locale, exception);
            }
        } else {
            log.warn("Message source is not initialized.");
        }
        return status.getReasonPhrase();
    }
}
