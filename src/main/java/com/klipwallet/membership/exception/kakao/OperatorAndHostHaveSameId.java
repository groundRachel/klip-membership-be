package com.klipwallet.membership.exception.kakao;

import lombok.NonNull;

import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.InvalidRequestException;

public class OperatorAndHostHaveSameId extends InvalidRequestException {
    public OperatorAndHostHaveSameId(@NonNull Long operatorId) {
        super(ErrorCode.OPERATOR_AND_HOST_HAVE_SAME_ID, operatorId);
    }
}
