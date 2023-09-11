package com.klipwallet.membership.adaptor.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import feign.Response;

import com.klipwallet.membership.dto.InternalApiError;
import com.klipwallet.membership.exception.kakao.KakaoBadRequestInternalApiException;
import com.klipwallet.membership.exception.kakao.KakaoForbiddenInternalApiException;
import com.klipwallet.membership.exception.kakao.KakaoUnknownInternalApiException;

public record KakaoError(@JsonProperty("code") int code, @JsonProperty("msg") String msg)
        implements InternalApiError {

    @Override
    public String getCode() {
        return Integer.toString(code);
    }

    @Override
    public String getMessage() {
        return msg;
    }

    public Exception convertException(Response response) {
        int status = response.status();
        if (status == 400) {
            return new KakaoBadRequestInternalApiException(this);
        }
        if (status == 401) {
            return new KakaoForbiddenInternalApiException(this);
        }
        return new KakaoUnknownInternalApiException(this);
    }
}