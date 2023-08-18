package com.klipwallet.membership.exception.member;

import com.klipwallet.membership.entity.AppliedPartner;
import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.InvalidRequestException;

public class PartnerApplicationAlreadyProcessedException extends InvalidRequestException {
    public PartnerApplicationAlreadyProcessedException(AppliedPartner appliedPartner) {
        super(ErrorCode.INVALID_REQUEST_APPROVE_ALREADY_PROCESSED,
              appliedPartner.getId(),
              appliedPartner.getStatus().toDisplay(),
              appliedPartner.getUpdatedBy(),
              appliedPartner.getUpdatedAt()
        );
    }
}
