package com.klipwallet.membership.exception.kakao;

import jakarta.validation.constraints.NotNull;

import lombok.NonNull;

import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.InvalidRequestException;

public class OperatorNotInPartnerException extends InvalidRequestException {
    public OperatorNotInPartnerException(@NonNull Long operatorId, @NotNull MemberId partnerId) {
        super(ErrorCode.OPERATOR_NOT_IN_PARTNER, operatorId, partnerId);
    }
}
