package com.klipwallet.membership.config.security;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.klipwallet.membership.controller.GlobalRestControllerAdvice;
import com.klipwallet.membership.exception.ErrorCode;

@RequiredArgsConstructor
public class ProblemDetailEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper mapper;
    private final MessageSource messageSource;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        ErrorCode unauthenticated = ErrorCode.UNAUTHENTICATED;
        String message = messageSource.getMessage(unauthenticated.toMessageCode(), null, LocaleContextHolder.getLocale());
        ProblemDetail problemDetail = GlobalRestControllerAdvice.toProblemDetail(unauthenticated, HttpStatus.UNAUTHORIZED, message);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        mapper.writeValue(response.getOutputStream(), problemDetail);
        response.flushBuffer();
    }
}
