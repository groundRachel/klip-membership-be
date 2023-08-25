package com.klipwallet.membership.exception;

import lombok.ToString;

import com.klipwallet.membership.adaptor.biztalk.BgmsTokenRes;
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

    private InternalApiException(String service, InternalApiError error) {
        super(ErrorCode.INTERNAL_API_ERROR);
        this.service = service;
        this.error = error;
    }

    public static InternalApiException biztalk(BgmsTokenRes res) {
        return new InternalApiException("biztalk", res);
    }
}
