package com.alihasanov.courierpay.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;
import java.util.Map;

@Slf4j
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ErrorResponse errorResponse;
    private final Map<String, Object> messageArguments;

    public ApplicationException(ErrorResponse errorResponse, Map<String, Object> messageArguments) {
        super(errorResponse.getMessage());
        this.errorResponse = errorResponse;
        this.messageArguments = messageArguments;
    }

    public ApplicationException(ErrorResponse errorResponse, Map<String, Object> messageArguments, Throwable cause) {
        super(cause);
        this.errorResponse = errorResponse;
        this.messageArguments = messageArguments;
    }

    public ApplicationException(ErrorResponse errorResponse) {
        super(errorResponse.getMessage());
        this.errorResponse = errorResponse;
        this.messageArguments = Map.of();
    }

    public ApplicationException(ErrorResponse errorResponse, Throwable cause) {
        super(cause);
        this.errorResponse = errorResponse;
        this.messageArguments = Map.of();
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

    @Override
    public String getMessage() {
        if (errorResponse == null) {
            return super.getMessage();
        }
        if (messageArguments.isEmpty()) {
            return errorResponse.getMessage();
        }

        String message = errorResponse.getMessage();
        for (Map.Entry<String, Object> entry : messageArguments.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
            message = message.replace("[" + entry.getKey() + "]", String.valueOf(entry.getValue()));
        }
        return message;
    }

    public Map<String, Object> getMessageArguments() {
        return messageArguments;
    }

    public String getLocalizedMessage(Locale locale, MessageSource messageSource) {
        if (errorResponse == null || errorResponse.getKey() == null || messageSource == null) {
            log.warn("ErrorResponse, error key, or MessageSource is null. Returning default message.");
            return getMessage();
        }

        try {
            String localizedMessage = messageSource.getMessage(errorResponse.getKey(), new Object[]{}, locale);
            if (messageArguments.isEmpty()) {
                return localizedMessage;
            }
            for (Map.Entry<String, Object> entry : messageArguments.entrySet()) {
                localizedMessage = localizedMessage.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
                localizedMessage = localizedMessage.replace("[" + entry.getKey() + "]", String.valueOf(entry.getValue()));
            }
            return localizedMessage;
        } catch (NoSuchMessageException exception) {
            log.warn("Please consider adding localized message for key {} and locale {}",
                    errorResponse.getKey(), locale, exception);
        }

        return getMessage();
    }
}
