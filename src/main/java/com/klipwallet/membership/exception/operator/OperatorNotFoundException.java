package com.klipwallet.membership.exception.operator;

import lombok.NonNull;

import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.NotFoundException;

public class OperatorNotFoundException extends NotFoundException {
    public OperatorNotFoundException(@NonNull Long operatorId) {
        super(ErrorCode.OPERATOR_NOT_FOUND, operatorId);
    }
}
