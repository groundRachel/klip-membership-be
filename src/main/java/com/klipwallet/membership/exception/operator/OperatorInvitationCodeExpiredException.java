package com.klipwallet.membership.exception.operator;

import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.ExpiredException;

/**
 * 운영진 초대 코드가 만료됐습니다.(30분)
 */
@SuppressWarnings("serial")
public class OperatorInvitationCodeExpiredException extends ExpiredException {
    public OperatorInvitationCodeExpiredException() {
        super(ErrorCode.OPERATOR_INVITATION_CODE_EXPIRED);
    }
}
