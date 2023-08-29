package com.klipwallet.membership.entity;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * 파트너 신청 승인됨 DomainEvent
 *
 * @see com.klipwallet.membership.entity.PartnerApplication#approve(MemberId)
 * @see com.klipwallet.membership.service.PartnerService#subscribePartnerApplicationApproved(PartnerApplicationApproved)
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class PartnerApplicationApproved extends DomainEvent {
    Integer partnerApplicationId;
    PartnerApplication partnerApplication;
    MemberId occurrerId;

    // TODO KLDV-3069 send result by email
}
