package com.klipwallet.membership.controller;

import java.net.URI;
import java.util.Locale;

import jakarta.annotation.Nonnull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.UriComponentsBuilder;

import com.klipwallet.membership.exception.BaseMessageException;
import com.klipwallet.membership.exception.NotFoundException;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalRestControllerAdvice {
    private final MessageSource messageSource;

    @Nonnull
    public static ProblemDetail toProblemDetail(HttpStatus status, Exception cause, String code, String message) {
        ProblemDetail result = ProblemDetail.forStatusAndDetail(status, message);
        String typeName = cause.getClass().getSimpleName();
        result.setType(UriComponentsBuilder.fromHttpUrl("https://membership.klipwallet.com/errors/").path(typeName).build().toUri());
        result.setTitle(typeName);
        result.setProperty("code", code);
        result.setProperty("err", message);
        return result;
    }

    @Nonnull
    public static ProblemDetail toProblemDetail(@Nonnull OAuth2AuthenticationException cause) {
        OAuth2Error error = cause.getError();
        String typeName = cause.getClass().getSimpleName();
        ProblemDetail result = ProblemDetail.forStatusAndDetail(UNAUTHORIZED, error.getDescription());
        result.setType(URI.create(error.getUri()));
        result.setTitle(typeName);
        result.setProperty("code", 401000);
        result.setProperty("providerCode", error.getErrorCode());
        result.setProperty("err", error.getDescription());
        return result;
    }

    @Nonnull
    public static ProblemDetail toProblemDetail(@Nonnull Exception cause) {
        String typeName = cause.getClass().getSimpleName();
        ProblemDetail result = ProblemDetail.forStatusAndDetail(INTERNAL_SERVER_ERROR, cause.getMessage());
        result.setType(UriComponentsBuilder.fromHttpUrl("https://membership.klipwallet.com/errors/50000").build().toUri());
        result.setTitle(typeName);
        result.setProperty("code", "500000");
        result.setProperty("err", cause.getMessage());
        return result;
    }

    @Nonnull
    private String tryGetMessage(@Nonnull BaseMessageException cause, @Nonnull String code) {
        try {
            String message = messageSource.getMessage(code, cause.getArgs(), Locale.getDefault());
            if (message.startsWith("error.")) {
                log.warn("[ProblemDetail] Error code does not exist. {}", code);
            }
            return message;
        } catch (NoSuchMessageException ex) {
            log.warn("[ProblemDetail] Error code does not exist. {}", code, ex);
        }
        return code;
    }

    @Nonnull
    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFoundException(NotFoundException cause) {
        String code = cause.getCode();
        String err = tryGetMessage(cause, code);
        return toProblemDetail(NOT_FOUND, cause, code, err);
    }
}
