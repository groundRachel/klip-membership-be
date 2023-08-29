package com.klipwallet.membership.exception;

@SuppressWarnings("serial")
public class OperatorNotFoundException extends NotFoundException {
    public OperatorNotFoundException(Integer operatorId) {
        super(ErrorCode.OPERATOR_NOT_FOUND, operatorId);
    }
}
