package com.klipwallet.membership.entity;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * 고정 공지 변경됨 DomainEvent
 * <p>
 * 고정 공지는 <b>단 하나만 설정할 수 있으므로</b> 기존 고정 공지는 비활성화 되어야함.
 * </p>
 *
 * @see Notice#update(NoticeUpdatable)
 * @see com.klipwallet.membership.service.NoticeService#subscribePrimaryNoticeChanged(PrimaryNoticeChanged)
 */
@EqualsAndHashCode(callSuper = false)
@Value
public class PrimaryNoticeChanged extends DomainEvent {
    Integer primaryNoticeId;
}
