package com.klipwallet.membership.controller.error;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import jakarta.annotation.Nonnull;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.klipwallet.membership.controller.GlobalRestControllerAdvice;

/**
 * <pre>
 * {
 *   "type": "https://membership-api.klipwallet.com/errors/NOTICE_NOT_FOUND",
 *   "title": "Not Found",
 *   "status": 404,
 *   "detail": "공지사항을 찾을 수 없습니다. ID: 3322",
 *   "code": 404001,
 *   "err": "공지사항을 찾을 수 없습니다. ID: 3322"
 * }
 * </pre>
 */
@SuppressWarnings("JavadocLinkAsPlainText")
@Component
@Slf4j
public class ProblemDetailErrorAttributes implements ErrorAttributes, HandlerExceptionResolver, Ordered {
    private static final String ERROR_INTERNAL_ATTRIBUTE = ProblemDetailErrorAttributes.class.getName() + ".ERROR";
    private final ObjectMapper objectMapper;

    public ProblemDetailErrorAttributes(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public ModelAndView resolveException(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, Object handler,
                                         @Nonnull Exception ex) {
        storeErrorAttributes(request, ex);
        return null;
    }

    private void storeErrorAttributes(HttpServletRequest request, Exception ex) {
        request.setAttribute(ERROR_INTERNAL_ATTRIBUTE, ex);
    }

    @SuppressWarnings("Convert2Diamond")
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        HttpStatus httpStatus = getHttpStatus(webRequest);
        Throwable error = getError(webRequest);
        ProblemDetail problemDetail = GlobalRestControllerAdvice.toProblemDetail(httpStatus, error);
        addErrorDetails(problemDetail, webRequest, options);
        addPath(problemDetail, webRequest);
        return objectMapper.convertValue(problemDetail, new TypeReference<Map<String, Object>>() {
        });
    }

    private HttpStatus getHttpStatus(WebRequest webRequest) {
        Integer status = getAttribute(webRequest, RequestDispatcher.ERROR_STATUS_CODE);
        try {
            return HttpStatus.valueOf(status);
        } catch (Exception cause) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private void addErrorDetails(ProblemDetail problemDetail, WebRequest webRequest, ErrorAttributeOptions options) {
        Throwable error = getError(webRequest);
        if (error != null) {
            while (error instanceof ServletException && error.getCause() != null) {
                error = error.getCause();
            }
            if (options.isIncluded(Include.EXCEPTION)) {
                problemDetail.setProperty("exception", error.getClass().getName());
            }
            if (options.isIncluded(Include.STACK_TRACE)) {
                addStackTrace(problemDetail, error);
            }
        }
        addErrorMessage(problemDetail, webRequest, error, options);
    }

    private void addErrorMessage(ProblemDetail problemDetail, WebRequest webRequest, Throwable error,
                                 ErrorAttributeOptions options) {
        BindingResult result = extractBindingResult(error);
        if (result == null) {
            if (options.isIncluded(Include.MESSAGE)) {
                addExceptionErrorMessage(problemDetail, webRequest, error);
            }
        } else {
            if (options.isIncluded(Include.BINDING_ERRORS)) {
                addBindingResultErrorMessage(problemDetail, result);
            }
        }
    }

    private void addExceptionErrorMessage(ProblemDetail problemDetail, WebRequest webRequest, Throwable error) {
        problemDetail.setProperty("message", getMessage(webRequest, error));
    }

    /**
     * Returns the message to be included as the value of the {@code message} error
     * attribute. By default the returned message is the first of the following that is
     * not empty:
     * <ol>
     * <li>Value of the {@link RequestDispatcher#ERROR_MESSAGE} request attribute.
     * <li>Message of the given {@code error}.
     * <li>{@code No message available}.
     * </ol>
     *
     * @param webRequest current request
     * @param error      current error, if any
     * @return message to include in the error attributes
     * @since 2.4.0
     */
    protected String getMessage(WebRequest webRequest, Throwable error) {
        Object message = getAttribute(webRequest, RequestDispatcher.ERROR_MESSAGE);
        if (!ObjectUtils.isEmpty(message)) {
            return message.toString();
        }
        if (error != null && StringUtils.hasLength(error.getMessage())) {
            return error.getMessage();
        }
        return "No message available";
    }

    private void addBindingResultErrorMessage(ProblemDetail problemDetail, BindingResult result) {
        problemDetail.setProperty("message", "Validation failed for object='" + result.getObjectName() + "'. "
                                             + "Error count: " + result.getErrorCount());
        problemDetail.setProperty("errors", result.getAllErrors());
    }

    private BindingResult extractBindingResult(Throwable error) {
        if (error instanceof BindingResult bindingResult) {
            return bindingResult;
        }
        return null;
    }

    private void addStackTrace(ProblemDetail problemDetail, Throwable error) {
        StringWriter stackTrace = new StringWriter();
        error.printStackTrace(new PrintWriter(stackTrace));
        stackTrace.flush();
        problemDetail.setProperty("trace", stackTrace.toString());
    }

    private void addPath(ProblemDetail problemDetail, RequestAttributes requestAttributes) {
        String path = getAttribute(requestAttributes, RequestDispatcher.ERROR_REQUEST_URI);
        if (path != null) {
            problemDetail.setProperty("path", path);
        }
    }

    @Override
    public Throwable getError(WebRequest webRequest) {
        Throwable exception = getAttribute(webRequest, ERROR_INTERNAL_ATTRIBUTE);
        if (exception == null) {
            exception = getAttribute(webRequest, RequestDispatcher.ERROR_EXCEPTION);
        }
        webRequest.setAttribute(ErrorAttributes.ERROR_ATTRIBUTE, exception, WebRequest.SCOPE_REQUEST);
        return exception;
    }

    @SuppressWarnings("unchecked")
    private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
        return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }
}
