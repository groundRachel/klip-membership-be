package com.klipwallet.membership.exception.operator;

import com.klipwallet.membership.exception.ConflictException;
import com.klipwallet.membership.exception.ErrorCode;

/**
 * 이미 가입된 운영진입니다. {0}
 */
@SuppressWarnings("serial")
public class OperationAlreadyJoinedException extends ConflictException {
    public OperationAlreadyJoinedException() {
        super(ErrorCode.OPERATOR_ALREADY_JOINED);
    }
}
