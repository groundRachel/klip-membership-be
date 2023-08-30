package com.klipwallet.membership.entity;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * 파트너 신청 거절됨 DomainEvent
 *
 * @see com.klipwallet.membership.entity.PartnerApplication#reject(String, MemberId)
 * @see com.klipwallet.membership.service.PartnerApplicationEmailService#notifyRejectResult(PartnerApplicationRejected)
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class PartnerApplicationRejected extends DomainEvent {
    String email;
    String rejectReason;
}
