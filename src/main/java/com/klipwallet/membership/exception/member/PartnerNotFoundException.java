package com.klipwallet.membership.exception.member;

import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.NotFoundException;

public class PartnerNotFoundException extends NotFoundException {
    public PartnerNotFoundException(Integer id) {
        super(ErrorCode.PARTNER_NOT_FOUND, id);
    }
}
