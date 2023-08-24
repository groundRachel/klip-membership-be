package com.klipwallet.membership.controller.error;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.WebAttributes;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.controller.GlobalRestControllerAdvice;


@Hidden
@RestController
@RequestMapping("${server.error.path:${error.path:/error}}")
@ConditionalOnWebApplication
public class ProblemDetailErrorController extends AbstractErrorController {
    private final ErrorProperties errorProperties;

    public ProblemDetailErrorController(ErrorAttributes errorAttributes, ServerProperties serverProperties,
                                        List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorViewResolvers);
        this.errorProperties = serverProperties.getError();
    }

    @RequestMapping
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        if (status == HttpStatus.NO_CONTENT) {
            return new ResponseEntity<>(status);
        }
        Map<String, Object> body = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL));
        return new ResponseEntity<>(body, status);
    }

    @RequestMapping("/403")
    public ProblemDetail accessDenied(HttpServletRequest request) {
        AccessDeniedException cause = (AccessDeniedException) request.getAttribute(WebAttributes.ACCESS_DENIED_403);
        if (cause == null) {
            cause = new AccessDeniedException("Forbidden");
        }
        return GlobalRestControllerAdvice.toProblemDetail(cause);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ProblemDetail> mediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException cause, HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        ProblemDetail problemDetail = GlobalRestControllerAdvice.toProblemDetail(status, cause);
        return ResponseEntity.status(status).body(problemDetail);
    }

    @SuppressWarnings("SameParameterValue")
    protected ErrorAttributeOptions getErrorAttributeOptions(HttpServletRequest request, MediaType mediaType) {
        ErrorAttributeOptions options = ErrorAttributeOptions.defaults();
        if (this.errorProperties.isIncludeException()) {
            options = options.including(Include.EXCEPTION);
        }
        if (isIncludeStackTrace(request, mediaType)) {
            options = options.including(Include.STACK_TRACE);
        }
        if (isIncludeMessage(request, mediaType)) {
            options = options.including(Include.MESSAGE);
        }
        if (isIncludeBindingErrors(request, mediaType)) {
            options = options.including(Include.BINDING_ERRORS);
        }
        return options;
    }

    /**
     * Determine if the stacktrace attribute should be included.
     *
     * @param request  the source request
     * @param produces the media type produced (or {@code MediaType.ALL})
     * @return if the stacktrace attribute should be included
     */
    @SuppressWarnings("unused")
    protected boolean isIncludeStackTrace(HttpServletRequest request, MediaType produces) {
        return switch (getErrorProperties().getIncludeStacktrace()) {
            case ALWAYS -> true;
            case ON_PARAM -> getTraceParameter(request);
            default -> false;
        };
    }

    /**
     * Determine if the message attribute should be included.
     *
     * @param request  the source request
     * @param produces the media type produced (or {@code MediaType.ALL})
     * @return if the message attribute should be included
     */
    @SuppressWarnings("unused")
    protected boolean isIncludeMessage(HttpServletRequest request, MediaType produces) {
        return switch (getErrorProperties().getIncludeMessage()) {
            case ALWAYS -> true;
            case ON_PARAM -> getMessageParameter(request);
            default -> false;
        };
    }

    /**
     * Determine if the errors attribute should be included.
     *
     * @param request  the source request
     * @param produces the media type produced (or {@code MediaType.ALL})
     * @return if the errors attribute should be included
     */
    @SuppressWarnings("unused")
    protected boolean isIncludeBindingErrors(HttpServletRequest request, MediaType produces) {
        return switch (getErrorProperties().getIncludeBindingErrors()) {
            case ALWAYS -> true;
            case ON_PARAM -> getErrorsParameter(request);
            default -> false;
        };
    }

    /**
     * Provide access to the error properties.
     *
     * @return the error properties
     */
    protected ErrorProperties getErrorProperties() {
        return this.errorProperties;
    }
}
