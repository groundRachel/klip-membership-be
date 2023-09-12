package com.klipwallet.membership.exception.kakao;

import lombok.ToString;

import com.klipwallet.membership.dto.InternalApiError;
import com.klipwallet.membership.exception.BaseCodeException;
import com.klipwallet.membership.exception.ErrorCode;

@SuppressWarnings("serial")
@ToString(callSuper = true)
public class KakaoUnknownInternalApiException extends BaseCodeException {
    private final InternalApiError error;

    @SuppressWarnings("unused")
    public KakaoUnknownInternalApiException(InternalApiError error) {
        super(ErrorCode.INTERNAL_API_ERROR);
        this.error = error;
    }
}
