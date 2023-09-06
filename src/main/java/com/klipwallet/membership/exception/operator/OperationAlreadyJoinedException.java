package com.klipwallet.membership.exception.operator;

import com.klipwallet.membership.exception.ConflictException;
import com.klipwallet.membership.exception.ErrorCode;

@SuppressWarnings("serial")
public class OperationAlreadyJoinedException extends ConflictException {
    public OperationAlreadyJoinedException() {
        super(ErrorCode.OPERATOR_ALREADY_JOINED);
    }
}
