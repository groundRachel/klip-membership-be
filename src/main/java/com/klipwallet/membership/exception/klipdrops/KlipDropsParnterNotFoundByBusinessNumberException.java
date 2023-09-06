package com.klipwallet.membership.exception.klipdrops;

import lombok.NonNull;

import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.NotFoundException;

public class KlipDropsParnterNotFoundByBusinessNumberException extends NotFoundException {
    public KlipDropsParnterNotFoundByBusinessNumberException(@NonNull Integer PartnerApplicationId, String businessRegistrationNumber) {
        super(ErrorCode.KLIP_DROPS_PARTNER_NOT_FOUND_BY_BUSINESS_NUMBER, PartnerApplicationId, businessRegistrationNumber);
    }
}
