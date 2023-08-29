package com.klipwallet.membership.exception.member;

import com.klipwallet.membership.exception.ConflictException;
import com.klipwallet.membership.exception.ErrorCode;

public class PartnerApplicationDuplicatedException extends ConflictException {

    public PartnerApplicationDuplicatedException() {
        super(ErrorCode.PARTNER_APPLICATION_DUPLICATED);
    }
}
