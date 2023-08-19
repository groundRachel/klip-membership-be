package com.klipwallet.membership.exception.member;

import lombok.NonNull;

import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.NotFoundException;

public class PartnerNotFoundException extends NotFoundException {
    public PartnerNotFoundException(@NonNull MemberId partnerId) {
        super(ErrorCode.PARTNER_NOT_FOUND, partnerId.value());
    }
}
