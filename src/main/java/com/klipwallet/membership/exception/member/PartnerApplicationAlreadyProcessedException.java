package com.klipwallet.membership.exception.member;

import java.time.LocalDateTime;

import com.klipwallet.membership.entity.AppliedPartner;
import com.klipwallet.membership.entity.AppliedPartner.Status;
import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.InvalidRequestException;

public class PartnerApplicationAlreadyProcessedException extends InvalidRequestException {
    public PartnerApplicationAlreadyProcessedException(AppliedPartner appliedPartner) {
        super(ErrorCode.INVALID_REQUEST_ACCEPT_ALREADY_PROCESSED,
              appliedPartner.getId(),
              appliedPartner.getStatus().toDisplay(),
              appliedPartner.getUpdatedBy(),
              appliedPartner.getUpdatedAt()
        );
    }
}
