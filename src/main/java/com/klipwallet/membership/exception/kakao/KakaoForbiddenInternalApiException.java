package com.klipwallet.membership.exception.kakao;

import lombok.ToString;

import com.klipwallet.membership.dto.InternalApiError;
import com.klipwallet.membership.exception.BaseCodeException;
import com.klipwallet.membership.exception.ErrorCode;

@SuppressWarnings("serial")
@ToString(callSuper = true)
public class KakaoForbiddenInternalApiException extends BaseCodeException {
    private final InternalApiError error;

    @SuppressWarnings("unused")
    public KakaoForbiddenInternalApiException(InternalApiError error) {
        super(ErrorCode.KAKAO_FORBIDDEN_INTERNAL_API_ERROR);
        this.error = error;
    }
}
