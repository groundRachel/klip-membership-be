package com.klipwallet.membership.adaptor.kakao.biztalk;

import lombok.EqualsAndHashCode;
import lombok.Value;

import com.klipwallet.membership.entity.DomainEvent;

/**
 * BGMS 사용자 토큰이 곧 만료됨 이벤트
 * <p>
 * 사용자 토큰이 곧 만료되므로 background로 토큰을 재발급 시도.
 * </p>
 *
 * @see BgmsInvitationNotificationAdaptor
 */
@EqualsAndHashCode(callSuper = true)
@Value
public class BgmsTokenExpiredSoon extends DomainEvent {
    BgmsToken oldToken;
}
