package com.klipwallet.membership.exception.member;

import lombok.NonNull;

import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.NotFoundException;

public class KlipDropsPartnerIdNotFound extends NotFoundException {
    public KlipDropsPartnerIdNotFound(@NonNull MemberId partnerId) {
        super(ErrorCode.PARTNER_KLIP_DROPS_PARTNER_ID_NOT_FOUND, partnerId.value());
    }
}
