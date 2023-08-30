package com.klipwallet.membership.exception.kakao;

import lombok.NonNull;

import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.InvalidRequestException;

public class OperatorNotInPartnerAccountException extends InvalidRequestException {
    public OperatorNotInPartnerAccountException(@NonNull Long operatorId) {
        super(ErrorCode.OPERATOR_NOT_IN_PARTNER_ACCOUNT, operatorId);
    }
}
