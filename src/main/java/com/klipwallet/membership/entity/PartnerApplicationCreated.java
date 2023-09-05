package com.klipwallet.membership.entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 파트너 요청됨 DomainEvent
 *
 * @see com.klipwallet.membership.entity.PartnerApplication#apply()
 * @see com.klipwallet.membership.service.PartnerApplicationService#getAndSetKlipDropsPartnerInfo(PartnerApplicationCreated)
 */

@EqualsAndHashCode(callSuper = false)
@ToString
public class PartnerApplicationCreated extends DomainEvent {
    private final PartnerApplication entity;

    public PartnerApplicationCreated(PartnerApplication entity) {
        this.entity = entity;
    }

    public Integer getPartnerApplicationId() {
        return this.entity.getId();
    }
}
