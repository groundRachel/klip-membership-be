package com.klipwallet.membership.exception.member;

import com.klipwallet.membership.entity.AppliedPartner;
import com.klipwallet.membership.exception.ConflictException;
import com.klipwallet.membership.exception.ErrorCode;

public class PartnerApplicationAlreadyProcessedException extends ConflictException {
    public PartnerApplicationAlreadyProcessedException(AppliedPartner appliedPartner) {
        super(ErrorCode.PARTNER_APPLICATION_ALREADY_PROCESSED,
              appliedPartner.getId(),
              appliedPartner.getStatus().toDisplay(),
              appliedPartner.getUpdatedBy(),
              appliedPartner.getUpdatedAt()
        );
    }
}
