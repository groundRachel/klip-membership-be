package com.klipwallet.membership.controller.error;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJacksonInputMessage;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.klipwallet.membership.dto.notice.NoticeDto;
import com.klipwallet.membership.entity.Notice;

@RestController
@RequestMapping("/error/raise")
public class ErrorRaiseController {
    @GetMapping("/1")
    public void raiseHttpRequestMethodNotSupportedException() throws Exception {
        throw new HttpRequestMethodNotSupportedException("POST", List.of("HEAD", "GET"));
    }

    @GetMapping("/14")
    public void raiseHttpMediaTypeNotSupportedException() throws Exception {
        throw new HttpMediaTypeNotSupportedException(MediaType.TEXT_XML, List.of(MediaType.APPLICATION_JSON));
    }

    @GetMapping("/2")
    public void raiseHttpMediaTypeNotAcceptableException() throws Exception {
        throw new HttpMediaTypeNotAcceptableException(List.of(MediaType.APPLICATION_JSON));
    }

    /**
     * `{@code GET /raise/3}` 으로 호출하면 MissingPathVariableException 예외가 발생한다.
     */
    @SuppressWarnings("unused")
    @GetMapping({"/3/{code}", "/3"})
    public void raiseMissingPathVariableException(@PathVariable String code) {
    }

    @GetMapping("/4")
    public void raiseMissingServletRequestParameterException() throws Exception {
        throw new MissingServletRequestParameterException("code", "Integer", false);
    }

    @GetMapping("/5")
    public void raiseMissingServletRequestPartException() throws Exception {
        throw new MissingServletRequestPartException("somePart");
    }

    @GetMapping("/6")
    public void raiseServletRequestBindingException(@SuppressWarnings("unused") @RequestHeader("X-Some-Header") String someHeader) {
    }

    @GetMapping("/7")
    public void raiseNoHandlerFoundException() throws Exception {
        throw new NoHandlerFoundException("GET", "/7/no-handler", HttpHeaders.EMPTY);
    }

    @GetMapping("/8")
    public void raiseAsyncRequestTimeoutException() {
        throw new AsyncRequestTimeoutException();
    }

    @GetMapping("/9")
    public void raiseErrorResponseException() {
        throw new ErrorResponseException(HttpStatus.GONE,
                                         ProblemDetail.forStatusAndDetail(HttpStatus.GONE, "Gone"),
                                         new RuntimeException("Nested Exception"));
    }

    @GetMapping("/10")
    public void raiseConversionNotSupportedException() {
        throw new ConversionNotSupportedException(
                "lived", Notice.Status.class,
                new IllegalArgumentException("No enum constant " + Notice.Status.class.getCanonicalName() + "." + "lived"));
    }

    @GetMapping("/11")
    public void raiseTypeMismatchException(@RequestParam("code") Integer code) {
    }

    @GetMapping("/12")
    public void raiseHttpMessageNotReadableException() {
        throw new HttpMessageNotReadableException("Cannot unmarshal to [" + NoticeDto.Create.class.getCanonicalName() + "]",
                                                  new MappingJacksonInputMessage(InputStream.nullInputStream(), HttpHeaders.EMPTY));
    }

    @GetMapping("/13")
    public void raiseHttpMessageNotWritableException() {
        throw new HttpMessageNotWritableException("Cannot marshal to [" + NoticeDto.Create.class.getCanonicalName() + "]");
    }
}
