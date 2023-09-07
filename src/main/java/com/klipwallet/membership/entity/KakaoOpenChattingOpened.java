package com.klipwallet.membership.entity;

import lombok.EqualsAndHashCode;
import lombok.Value;

import com.klipwallet.membership.entity.kakao.KakaoOpenlinkSummary;


/**
 * 카카오 오픈 채팅방이 개설됨 DomainEvent
 * <p>
 * 카카오 오픈 채팅방이 개설됐지만 Rollback 된 경우 개설된 카카오 채팅방을 삭제하기 위해서 발행하는 이벤트
 * </p>
 *
 * @see com.klipwallet.membership.service.OpenChattingService#create(com.klipwallet.membership.dto.openchatting.OpenChattingCreate, AuthenticatedUser)
 * @see com.klipwallet.membership.adaptor.kakao.KakaoAdaptor#subscribeOnRollback(KakaoOpenChattingOpened)
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class KakaoOpenChattingOpened extends DomainEvent {
    KakaoOpenlinkSummary kakaoOpenlinkSummary;
}
