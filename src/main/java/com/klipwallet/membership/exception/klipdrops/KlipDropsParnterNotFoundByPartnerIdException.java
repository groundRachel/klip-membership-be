package com.klipwallet.membership.exception.klipdrops;

import lombok.NonNull;

import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.NotFoundException;

public class KlipDropsParnterNotFoundByPartnerIdException extends NotFoundException {
    public KlipDropsParnterNotFoundByPartnerIdException(@NonNull Integer partnerId) {
        super(ErrorCode.KLIP_DROPS_PARTNER_NOT_FOUND_BY_PARTNER_ID, partnerId);
    }
}
