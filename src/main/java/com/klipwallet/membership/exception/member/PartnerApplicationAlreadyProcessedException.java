package com.klipwallet.membership.exception.member;

import java.time.LocalDateTime;

import com.klipwallet.membership.entity.AppliedPartner;
import com.klipwallet.membership.entity.AppliedPartner.Status;
import com.klipwallet.membership.exception.BaseMessageException;

public class PartnerApplicationAlreadyProcessedException extends BaseMessageException {
    public PartnerApplicationAlreadyProcessedException(AppliedPartner appliedPartner) {
        super("ID %d에 대한 정보는 이미 처리되었습니다. (처리 상태 : %s, 처리자 : %d, 처리 시각: %s)".formatted(appliedPartner.getId(), appliedPartner.getStatus(),
                                                                                      appliedPartner.getUpdatedBy(),
                                                                                      appliedPartner.getUpdatedAt().toString()));
    }
}
