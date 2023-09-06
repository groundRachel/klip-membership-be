package com.klipwallet.membership.exception.member;

import lombok.NonNull;

import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.NotFoundException;

public class PartnerUnlinkedToKlipDropsPartnerException extends NotFoundException {
    public PartnerUnlinkedToKlipDropsPartnerException(@NonNull MemberId partnerId) {
        super(ErrorCode.PARTNER_UNLIKED_TO_KLIP_DROPS_PARTNER, partnerId.value());
    }
}
