package com.klipwallet.membership.exception.member;

import com.klipwallet.membership.entity.PartnerApplication;
import com.klipwallet.membership.exception.ConflictException;
import com.klipwallet.membership.exception.ErrorCode;

public class PartnerApplicationAlreadyProcessedException extends ConflictException {
    public PartnerApplicationAlreadyProcessedException(PartnerApplication partnerApplication) {
        super(ErrorCode.PARTNER_APPLICATION_ALREADY_PROCESSED,
              partnerApplication.getId(),
              partnerApplication.getStatus().toDisplay(),
              partnerApplication.getUpdaterId().value(),
              partnerApplication.getUpdatedAt()
        );
    }
}
