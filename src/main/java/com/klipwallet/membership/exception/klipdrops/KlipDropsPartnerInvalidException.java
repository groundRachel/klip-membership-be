package com.klipwallet.membership.exception.klipdrops;

import lombok.NonNull;

import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.NotFoundException;

public class KlipDropsPartnerInvalidException extends NotFoundException {
    public KlipDropsPartnerInvalidException(@NonNull Integer partnerId) {
        super(ErrorCode.KLIP_DROPS_PARTNER_NOT_FOUND, partnerId);
    }
}
