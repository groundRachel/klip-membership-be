package com.klipwallet.membership.service.kakao;

/**
 * 카카오 톡 관련 서비스
 * <p>
 * 알림톡
 * </p>
 */
public interface KakaoBizMessageService {
    /**
     * 알림톡 발송
     *
     * @param phoneNumber 수신자 휴대폰 번호
     * @param message     발송 메시지
     */
    void sendNotificationTalk(String phoneNumber, NotificationTalkMessage message);
}
