package com.klipwallet.membership.adaptor.kas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import feign.Response;

import com.klipwallet.membership.dto.InternalApiError;
import com.klipwallet.membership.exception.InternalApiException;
import com.klipwallet.membership.exception.InvalidRequestException;
import com.klipwallet.membership.exception.NotFoundException;

public record KasError(@JsonProperty("code") int code, @JsonProperty("message") String message, @JsonProperty("requestId") String requestId)
        implements InternalApiError {

    @Override
    public String getCode() {
        return Integer.toString(code);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Exception convertException(Response response) {
        int status = response.status();
        if (status == 404) {
            return new NotFoundException();
        }
        if (status == 400) {
            return new InvalidRequestException(message);
        }
        return new InternalApiException(this);
    }
}