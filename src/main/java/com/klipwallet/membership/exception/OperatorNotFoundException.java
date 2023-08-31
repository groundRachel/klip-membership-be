package com.klipwallet.membership.exception;

import lombok.NonNull;

@SuppressWarnings("serial")
public class OperatorNotFoundException extends NotFoundException {
    public OperatorNotFoundException(@NonNull Integer operatorId) {
        super(ErrorCode.OPERATOR_NOT_FOUND, operatorId);
    }
}
