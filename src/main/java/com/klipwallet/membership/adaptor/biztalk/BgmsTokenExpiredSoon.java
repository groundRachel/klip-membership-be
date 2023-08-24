package com.klipwallet.membership.adaptor.biztalk;

import lombok.EqualsAndHashCode;
import lombok.Value;

import com.klipwallet.membership.entity.DomainEvent;

/**
 * BGMS 토큰이 곧 만료됨 이벤트
 *
 * @see com.klipwallet.membership.adaptor.biztalk.BgmsInvitationNotificationAdaptor
 */
@EqualsAndHashCode(callSuper = true)
@Value
public class BgmsTokenExpiredSoon extends DomainEvent {
    BgmsToken oldToken;
}
