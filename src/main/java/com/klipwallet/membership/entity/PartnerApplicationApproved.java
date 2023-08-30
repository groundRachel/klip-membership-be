package com.klipwallet.membership.entity;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * 파트너 신청 승인됨 DomainEvent
 *
 * @see com.klipwallet.membership.entity.PartnerApplication#approve(MemberId)
 * @see com.klipwallet.membership.service.ApplicationResultEmailService#notifyApproveResult(PartnerApplicationApproved)
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class PartnerApplicationApproved extends DomainEvent {
    String email;
}
