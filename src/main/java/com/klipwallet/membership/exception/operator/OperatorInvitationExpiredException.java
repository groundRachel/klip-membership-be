package com.klipwallet.membership.exception.operator;

import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.ExpiredException;

/**
 * 운영진 초대 기한이 만료됐습니다.(24시간)
 */
@SuppressWarnings("serial")
public class OperatorInvitationExpiredException extends ExpiredException {
    public OperatorInvitationExpiredException() {
        super(ErrorCode.OPERATOR_INVITATION_EXPIRED);
    }
}
