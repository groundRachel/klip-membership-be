package com.klipwallet.membership.exception.kas;

import lombok.ToString;

import com.klipwallet.membership.dto.InternalApiError;
import com.klipwallet.membership.exception.BaseCodeException;
import com.klipwallet.membership.exception.ErrorCode;

@SuppressWarnings("serial")
@ToString(callSuper = true)
public class KasBadRequestInternalApiException extends BaseCodeException {
    private final InternalApiError error;

    @SuppressWarnings("unused")
    public KasBadRequestInternalApiException(InternalApiError error) {
        super(ErrorCode.KAS_BADREQUEST_INTERNAL_API_ERROR);
        this.error = error;
    }
}
