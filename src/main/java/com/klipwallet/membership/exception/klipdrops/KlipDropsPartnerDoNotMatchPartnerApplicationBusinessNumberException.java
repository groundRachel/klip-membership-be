package com.klipwallet.membership.exception.klipdrops;

import jakarta.validation.constraints.NotNull;

import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.InvalidRequestException;

public class KlipDropsPartnerDoNotMatchPartnerApplicationBusinessNumberException extends InvalidRequestException {
    public KlipDropsPartnerDoNotMatchPartnerApplicationBusinessNumberException(@NotNull Integer partnerApplicationId,
                                                                               @NotNull Integer klipDropsPartnerId) {
        super(ErrorCode.KLIP_DROPS_PARTNER_DO_NOT_MATCH_PARNTER_APPLICATION_BUSINESS_NUMBER, partnerApplicationId, klipDropsPartnerId);
    }
}
