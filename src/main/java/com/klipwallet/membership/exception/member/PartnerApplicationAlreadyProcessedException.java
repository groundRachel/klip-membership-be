package com.klipwallet.membership.exception.member;

import java.time.LocalDateTime;

import com.klipwallet.membership.entity.AppliedPartner;
import com.klipwallet.membership.entity.AppliedPartner.Status;
import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.InvalidRequestException;

public class PartnerApplicationAlreadyProcessedException extends InvalidRequestException {
    public PartnerApplicationAlreadyProcessedException(AppliedPartner appliedPartner) {
        super(ErrorCode.INVALID_REQUEST_ACCEPT_ALREADY_PROCESSED,
              "ID %d에 대한 정보는 이미 처리되었습니다. (처리 상태 : %s, 처리자 : %s, 처리 시각: %s)".formatted(appliedPartner.getId(), appliedPartner.getStatus(),
                                                                                      appliedPartner.getUpdatedBy(),
                                                                                      appliedPartner.getUpdatedAt().toString()));
    }
}
