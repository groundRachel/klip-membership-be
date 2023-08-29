package com.klipwallet.membership.entity;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * 파트너 신청 거절됨 DomainEvent
 *
 * @see com.klipwallet.membership.entity.PartnerApplication#reject(String, MemberId)
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class PartnerApplicationRejected extends DomainEvent {
    PartnerApplication partnerApplication;
    MemberId occurrerId;

    // TODO KLDV-3069 send result by email
}
