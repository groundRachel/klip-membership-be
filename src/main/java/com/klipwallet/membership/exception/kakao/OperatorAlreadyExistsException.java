package com.klipwallet.membership.exception.kakao;

import lombok.NonNull;

import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.InvalidRequestException;

public class OperatorAlreadyExistsException extends InvalidRequestException {
    public OperatorAlreadyExistsException(@NonNull Long operatorId) {
        super(ErrorCode.OPERATOR_ALREADY_HOST_EXCEPTION, operatorId);
    }
}
