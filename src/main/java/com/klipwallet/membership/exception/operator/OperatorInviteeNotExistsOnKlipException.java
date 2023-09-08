package com.klipwallet.membership.exception.operator;

import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.InvalidRequestException;

/**
 * 초대한 운영진은 Klip 이용자가 아닙니다. {phoneNumber}
 */
@SuppressWarnings("serial")
public class OperatorInviteeNotExistsOnKlipException extends InvalidRequestException {
    public OperatorInviteeNotExistsOnKlipException(String phoneNumber) {
        super(ErrorCode.OPERATOR_INVITEE_NOT_EXISTS_ON_KLIP, phoneNumber);
    }
}
