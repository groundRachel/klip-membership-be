package com.klipwallet.membership.exception.member;

import lombok.NonNull;

import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.NotFoundException;

public class PartnerApplicationNotFoundException extends NotFoundException {
    public PartnerApplicationNotFoundException(@NonNull MemberId partnerId) {
        super(ErrorCode.PARTNER_APPLICATION_NOT_FOUND, partnerId.value());
    }
}
