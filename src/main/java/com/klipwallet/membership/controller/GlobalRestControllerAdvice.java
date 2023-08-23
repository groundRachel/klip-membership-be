package com.klipwallet.membership.controller;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.klipwallet.membership.controller.error.FieldErrorView;
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
    /**
     * ProblemDetail 추가 속성: err(String): 하위 오류들
     * <p>requelst body 입력 시 하나 이상의 필드에서 오류가 발생하는 경우.</p>
     */
    public static final String ERRORS = "errors";
    public static final String TYPE_URL_PATH = "https://membership-api.klipwallet.com/errors/";

    public static ProblemDetail toProblemDetail(HttpStatusCode status, BaseCodeException cause, String message) {
        ProblemDetail result = ProblemDetail.forStatusAndDetail(status, message);
        ErrorCode errorCode = cause.getErrorCode();
        result.setType(fromHttpUrl(TYPE_URL_PATH).path(errorCode.name()).build().toUri());
        result.setProperty(CODE, errorCode.getCode());
        result.setProperty(ERR, message);
        return result;
    }

    public static ProblemDetail toProblemDetail(HttpStatusCode statusCode, @Nullable Throwable cause) {
        ErrorCode errorCode = ErrorCode.fromStatusCode(statusCode);
        HttpStatus status = asHttpStatus(statusCode);
        String detail = cause != null ? cause.getMessage() : status.getReasonPhrase();
        ProblemDetail result = ProblemDetail.forStatusAndDetail(status, detail);
        result.setType(fromHttpUrl(TYPE_URL_PATH).path(errorCode.name()).build().toUri());
        result.setProperty(CODE, errorCode.getCode());
        result.setProperty(ERR, result.getDetail());
        return result;
    }

    @Nonnull
    public static ProblemDetail toProblemDetail(@Nonnull OAuth2AuthenticationException cause) {
        OAuth2Error error = cause.getError();
        ProblemDetail result = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, error.getDescription());
        result.setType(URI.create(error.getUri()));
        result.setProperty(CODE, ErrorCode.UNAUTHENTICATED_BY_OAUTH2.getCode());
        result.setProperty("providerCode", error.getErrorCode());
        result.setProperty(ERR, error.getDescription());
        return result;
    }

    @Nonnull
    public static ProblemDetail toProblemDetail(ErrorCode errorCode, HttpStatus httpStatus, String message) {
        ProblemDetail result = ProblemDetail.forStatusAndDetail(httpStatus, message);
        result.setType(fromHttpUrl(TYPE_URL_PATH).path(errorCode.name()).build().toUri());
        result.setProperty(CODE, errorCode.getCode());
        result.setProperty(ERR, message);
        return result;
    }

    @Nonnull
    public static ProblemDetail toProblemDetail(@Nonnull AccessDeniedException cause) {
        ErrorCode errorCode = ErrorCode.FORBIDDEN;
        ProblemDetail result = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, cause.getMessage());
        result.setType(fromHttpUrl(TYPE_URL_PATH).path(errorCode.name()).build().toUri());
        result.setProperty(CODE, errorCode.getCode());
        result.setProperty(ERR, cause.getMessage());
        return result;
    }

    @Nonnull
    public static ProblemDetail toProblemDetail(Throwable cause) {
        ProblemDetail result = ProblemDetail.forStatusAndDetail(INTERNAL_SERVER_ERROR, cause.getMessage());
        result.setType(fromHttpUrl(TYPE_URL_PATH).path(ErrorCode.INTERNAL_SERVER_ERROR.name()).build().toUri());
        result.setProperty(CODE, ErrorCode.INTERNAL_SERVER_ERROR.getCode());
        result.setProperty(ERR, cause.getMessage());
        return result;
    }

    private static HttpStatus asHttpStatus(HttpStatusCode status) {
        HttpStatus httpStatus = HttpStatus.resolve(status.value());
        if (httpStatus != null) {
            return httpStatus;
        }
        return INTERNAL_SERVER_ERROR;
    }

    private int httpStatusToDefaultCode(ProblemDetail result) {
        return result.getStatus() * 1000;
    }

    @Nonnull
    private String tryGetMessage(@Nonnull BaseCodeException cause) {
        return this.tryGetMessage(cause.getErrorCode(), cause.getErrorArgs());
    }

    @Nonnull
    private String tryGetMessage(@Nonnull ErrorCode errorCode) {
        return tryGetMessage(errorCode, null);
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
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException cause) {
        return toProblemDetail(cause);
    }

    public ProblemDetail toProblemDetail(ConstraintViolationException ex) {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST_BODY;
        Locale locale = LocaleContextHolder.getLocale();
        List<FieldErrorView> errors = getFieldErrorViews(ex, locale);
        String message = toMessage(errorCode, errors);

        ProblemDetail result = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        result.setType(fromHttpUrl(TYPE_URL_PATH).path(errorCode.name()).build().toUri());
        result.setProperty(CODE, errorCode.getCode());
        result.setProperty(ERR, message);
        result.setProperty(ERRORS, errors);
        return result;
    }

    private String toMessage(ErrorCode errorCode, List<FieldErrorView> errors) {
        if (errors.size() == 1) {
            return errors.get(0).getMessage();
        }
        return tryGetMessage(errorCode);
    }

    @SuppressWarnings("unused")
    private List<FieldErrorView> getFieldErrorViews(ConstraintViolationException ex, Locale locale) {
        return ex.getConstraintViolations().stream()
                 .map(this::toFieldErrorView)
                 .collect(Collectors.toList());
    }

    private FieldErrorView toFieldErrorView(ConstraintViolation<?> constraintViolation) {
        String field = constraintViolation.getPropertyPath().toString();
        String message = constraintViolation.getMessage();
        return new FieldErrorView(field, message);
    }

    @Nonnull
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException cause) {
        return toProblemDetail(HttpStatus.FORBIDDEN, cause);
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

    @Nonnull
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception cause) {
        return toProblemDetail(INTERNAL_SERVER_ERROR, cause);
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
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        return ResponseEntity.status(status).body(toProblemDetail(ex)); // 400
    }

    public ProblemDetail toProblemDetail(MethodArgumentNotValidException ex) {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST_BODY;
        Locale locale = LocaleContextHolder.getLocale();
        List<FieldErrorView> errors = getFieldErrorViews(ex, locale);
        String message = toMessage(errorCode, errors);

        ProblemDetail result = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), message);
        result.setType(fromHttpUrl(TYPE_URL_PATH).path(errorCode.name()).build().toUri());
        result.setProperty(CODE, errorCode.getCode());
        result.setProperty(ERR, message);
        result.setProperty(ERRORS, errors);
        return result;
    }

    private List<FieldErrorView> getFieldErrorViews(MethodArgumentNotValidException ex, Locale locale) {
        Map<ObjectError, String> messageMap = ex.resolveErrorMessages(messageSource(), locale);
        return ex.getFieldErrors().stream()
                 .map(f -> new FieldErrorView(f, messageMap.get(f)))
                 .collect(Collectors.toList());
    }

    @Override
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatusCode status,
                                                                        WebRequest request) {
        return ResponseEntity.status(status).body(toProblemDetail(ex)); // 503
    }

    public ProblemDetail toProblemDetail(AsyncRequestTimeoutException ex) {
        ErrorCode errorCode = ErrorCode.ASYNC_REQUEST_TIMEOUT;
        String message = tryGetMessage(errorCode);

        ProblemDetail result = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), message);
        result.setType(fromHttpUrl(TYPE_URL_PATH).path(errorCode.name()).build().toUri());
        result.setProperty(CODE, errorCode.getCode());
        result.setProperty(ERR, message);
        return result;
    }
}
