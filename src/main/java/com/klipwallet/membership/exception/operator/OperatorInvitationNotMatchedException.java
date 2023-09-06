package com.klipwallet.membership.exception.operator;

import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.InvalidRequestException;

/**
 * 운영진 초대자와 현재 접속자가 일치하지 않습니다.
 */
@SuppressWarnings("serial")
public class OperatorInvitationNotMatchedException extends InvalidRequestException {
    public OperatorInvitationNotMatchedException() {
        super(ErrorCode.OPERATOR_INVITATION_NOT_MATCHED);
    }

    public OperatorInvitationNotMatchedException(Throwable cause) {
        super(ErrorCode.OPERATOR_INVITATION_NOT_MATCHED, cause);
    }
}
