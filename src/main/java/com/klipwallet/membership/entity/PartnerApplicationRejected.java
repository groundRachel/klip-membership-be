package com.klipwallet.membership.entity;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class PartnerApplicationRejected extends DomainEvent {
    PartnerApplication partnerApplication;
    MemberId occurrerId;

    // TODO KLDV-3069 send result by email
}
