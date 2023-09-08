package com.klipwallet.membership.exception.kas;

import lombok.ToString;

import com.klipwallet.membership.dto.InternalApiError;
import com.klipwallet.membership.exception.BaseCodeException;
import com.klipwallet.membership.exception.ErrorCode;

@SuppressWarnings("serial")
@ToString(callSuper = true)
public class KasNotFoundInternalApiException extends BaseCodeException {
    private final InternalApiError error;

    @SuppressWarnings("unused")
    public KasNotFoundInternalApiException(InternalApiError error) {
        super(ErrorCode.KAS_NOTFOUND_INTERNAL_API_ERROR);
        this.error = error;
    }
}
