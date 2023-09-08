package com.klipwallet.membership.exception;

import lombok.ToString;

import com.klipwallet.membership.adaptor.kakao.biztalk.dto.BgmsBaseRes;
import com.klipwallet.membership.dto.InternalApiError;

@SuppressWarnings("serial")
@ToString(callSuper = true)
public class InternalApiException extends BaseCodeException {
    private final String service;
    private final InternalApiError error;


    @SuppressWarnings("unused")
    public InternalApiException(InternalApiError error) {
        super(ErrorCode.INTERNAL_API_ERROR);
        this.service = "unknown";
        this.error = error;
    }

    public InternalApiException(String service, InternalApiError error) {
        super(ErrorCode.INTERNAL_API_ERROR);
        this.service = service;
        this.error = error;
    }

    private InternalApiException(String service, Throwable cause) {
        super(ErrorCode.INTERNAL_API_ERROR, cause);
        this.service = service;
        this.error = InternalApiError.UNKNOWN;
    }

    public static InternalApiException biztalk(BgmsBaseRes res) {
        return new InternalApiException("biztalk", res);
    }

    public static InternalApiException biztalk(Throwable cause) {
        return new InternalApiException("biztalk", cause);
    }
}
