package com.klipwallet.membership.exception.operator;

import jakarta.validation.constraints.NotNull;

import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.InvalidRequestException;

public class OperatorDuplicatedException extends InvalidRequestException {
    public OperatorDuplicatedException(@NotNull Long operatorId) {
        super(ErrorCode.OPERATOR_DUPLICATED, operatorId);
    }
}
