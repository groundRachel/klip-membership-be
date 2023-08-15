package com.klipwallet.membership.entity;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * 메인 공지 활성화됨 DomainEvent
 * <p>
 * 메인 공지는 <b>단 하나만 활성화할 수 있으므로</b> 기존 메인 공지는 비활성화 되어야함.
 * </p>
 *
 * @see Notice#update(NoticeUpdatable)
 * @see com.klipwallet.membership.service.NoticeService#subscribeMainNoticeActivated(MainNoticeActivated)
 */
@EqualsAndHashCode(callSuper = false)
@Value
public class MainNoticeActivated extends DomainEvent {
    Integer noticeId;
}
