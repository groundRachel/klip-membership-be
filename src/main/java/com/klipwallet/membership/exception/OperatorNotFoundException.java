package com.klipwallet.membership.exception;

import lombok.NonNull;

public class OperatorNotFoundException extends NotFoundException {
    public OperatorNotFoundException(@NonNull Long operatorId) {
        super(ErrorCode.OPERATOR_NOT_FOUND, operatorId);
    }
}
