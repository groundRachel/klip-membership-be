package com.klipwallet.membership.config.security;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.klipwallet.membership.controller.GlobalRestControllerAdvice;
import com.klipwallet.membership.exception.ErrorCode;

@RequiredArgsConstructor
public class ProblemDetailEntryPoint implements AuthenticationEntryPoint, MessageSourceAware {
    private final ObjectMapper mapper;
    private MessageSource messageSource;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        ErrorCode unauthenticated = ErrorCode.UNAUTHENTICATED;
        String message = messageSource.getMessage(unauthenticated.toMessageCode(), null, LocaleContextHolder.getLocale());
        ProblemDetail problemDetail = GlobalRestControllerAdvice.toProblemDetail(unauthenticated, HttpStatus.UNAUTHORIZED, message);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        mapper.writeValue(response.getOutputStream(), problemDetail);
        response.flushBuffer();
    }

    @Override
    public void setMessageSource(@NonNull MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
