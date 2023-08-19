package com.klipwallet.membership.controller;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.klipwallet.membership.exception.BaseCodeException;
import com.klipwallet.membership.exception.ConflictException;
import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.InvalidRequestException;
import com.klipwallet.membership.exception.NotFoundException;

import static java.util.Objects.requireNonNullElse;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@SuppressWarnings("NullableProblems")
@RestControllerAdvice
@Slf4j
public class GlobalRestControllerAdvice extends ResponseEntityExceptionHandler {
    /**
     * ProblemDetail 추가 속성: code(int): 예외 코드
     */
    public static final String CODE = "code";
    /**
     * ProblemDetail 추가 속성: err(String): 예외 상세
     */
    public static final String ERR = "err";

    public static ProblemDetail toProblemDetail(HttpStatusCode status, BaseCodeException cause, String message) {
        ProblemDetail result = ProblemDetail.forStatusAndDetail(status, message);
        ErrorCode errorCode = cause.getErrorCode();
        result.setType(fromHttpUrl("https://membership.klipwallet.com/errors/").path(errorCode.name()).build().toUri());
        result.setProperty(CODE, errorCode.getCode());
        result.setProperty(ERR, message);
        return result;
    }

    public static ProblemDetail toProblemDetail(HttpStatusCode status, Exception cause, ErrorCode errorCode) {
        ProblemDetail result = ProblemDetail.forStatusAndDetail(status, cause.getMessage());
        result.setType(fromHttpUrl("https://membership.klipwallet.com/errors/").path(errorCode.name()).build().toUri());
        result.setProperty(CODE, errorCode.getCode());
        result.setProperty(ERR, result.getDetail());
        return result;
    }

    @Nonnull
    public static ProblemDetail toProblemDetail(@Nonnull OAuth2AuthenticationException cause) {
        OAuth2Error error = cause.getError();
        ProblemDetail result = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, error.getDescription());
        result.setType(URI.create(error.getUri()));
        result.setProperty(CODE, ErrorCode.UNAUTHENTICATED.getCode());
        result.setProperty("providerCode", error.getErrorCode());
        result.setProperty(ERR, error.getDescription());
        return result;
    }

    @Nonnull
    public static ProblemDetail toProblemDetail(@Nonnull Exception cause) {
        ProblemDetail result = ProblemDetail.forStatusAndDetail(INTERNAL_SERVER_ERROR, cause.getMessage());
        result.setType(fromHttpUrl("https://membership.klipwallet.com/errors").path(ErrorCode.INTERNAL_SERVER_ERROR.name()).build().toUri());
        result.setProperty(CODE, ErrorCode.INTERNAL_SERVER_ERROR.getCode());
        result.setProperty(ERR, cause.getMessage());
        return result;
    }

    private int httpStatusToDefaultCode(ProblemDetail result) {
        return result.getStatus() * 1000;
    }

    @Nonnull
    private String tryGetMessage(@Nonnull BaseCodeException cause) {
        return this.tryGetMessage(cause.getErrorCode(), cause.getErrorArgs());
    }

    @Nonnull
    private String tryGetMessage(@Nonnull ErrorCode errorCode, @Nullable Object[] args) {
        String code = errorCode.toMessageCode();
        try {
            String message = messageSource().getMessage(code, args, LocaleContextHolder.getLocale());
            if (message.startsWith("problemDetail.code")) {
                log.warn("[ProblemDetail] Error code does not exist. {}", code);
            }
            return message;
        } catch (NoSuchMessageException ex) {
            log.warn("[ProblemDetail] Error code does not exist. {}", code, ex);
            return code;
        }
    }

    @Nonnull
    @ExceptionHandler(InvalidRequestException.class)
    public ProblemDetail handleInvalidRequestException(InvalidRequestException cause) {
        String err = tryGetMessage(cause);
        return toProblemDetail(HttpStatus.BAD_REQUEST, cause, err);
    }

    @Nonnull
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException cause) {
        return toProblemDetail(HttpStatus.FORBIDDEN, cause, ErrorCode.FORBIDDEN);
    }

    @Nonnull
    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFoundException(NotFoundException cause) {
        String err = tryGetMessage(cause);
        return toProblemDetail(HttpStatus.NOT_FOUND, cause, err);
    }

    @Nonnull
    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflictException(ConflictException cause) {
        String err = tryGetMessage(cause);
        return toProblemDetail(HttpStatus.CONFLICT, cause, err);
    }

    private MessageSource messageSource() {
        return Objects.requireNonNull(this.getMessageSource(), "messageSource is null");
    }

    @Nonnull
    @Override
    protected ProblemDetail createProblemDetail(Exception ex, HttpStatusCode status, String defaultDetail, String detailMessageCode,
                                                Object[] detailMessageArguments, WebRequest request) {
        if (ex instanceof BaseCodeException bce) {
            return toProblemDetail(status, bce, tryGetMessage(bce));
        }
        ProblemDetail result = super.createProblemDetail(ex, status, defaultDetail, detailMessageCode, detailMessageArguments, request);
        result.setProperty(CODE, httpStatusToDefaultCode(result));
        result.setProperty(ERR, requireNonNullElse(result.getDetail(), defaultDetail));
        return result;
    }

    @Override
    protected ResponseEntity<Object> createResponseEntity(Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        ResponseEntity<Object> result = super.createResponseEntity(body, headers, statusCode, request);
        if (result.getBody() instanceof ProblemDetail problemDetail) {
            if (isKlipMembershipModel(problemDetail)) {
                return result;
            }
            ErrorCode errorCode = ErrorCode.fromStatusCode(statusCode);
            problemDetail.setProperty(CODE, errorCode.getCode());
            problemDetail.setProperty(ERR, problemDetail.getDetail());
        }
        return result;
    }

    private boolean isKlipMembershipModel(ProblemDetail problemDetail) {
        Map<String, Object> properties = problemDetail.getProperties();
        if (properties == null) {
            return false;
        }
        return properties.containsKey(CODE) && properties.containsKey(ERR);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers,
                                                                         HttpStatusCode status, WebRequest request) {
        return super.handleHttpRequestMethodNotSupported(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers,
                                                                     HttpStatusCode status, WebRequest request) {
        return super.handleHttpMediaTypeNotSupported(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers,
                                                                      HttpStatusCode status, WebRequest request) {
        return super.handleHttpMediaTypeNotAcceptable(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatusCode status,
                                                               WebRequest request) {
        return super.handleMissingPathVariable(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers,
                                                                          HttpStatusCode status, WebRequest request) {
        return super.handleMissingServletRequestParameter(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers,
                                                                     HttpStatusCode status, WebRequest request) {
        return super.handleMissingServletRequestPart(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers,
                                                                          HttpStatusCode status, WebRequest request) {
        return super.handleServletRequestBindingException(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        return super.handleMethodArgumentNotValid(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status,
                                                                   WebRequest request) {
        return super.handleNoHandlerFoundException(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatusCode status,
                                                                        WebRequest request) {
        return super.handleAsyncRequestTimeoutException(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleErrorResponseException(ErrorResponseException ex, HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        return super.handleErrorResponseException(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        return super.handleConversionNotSupported(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return super.handleTypeMismatch(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        return super.handleHttpMessageNotWritable(ex, headers, status, request);
    }
}
