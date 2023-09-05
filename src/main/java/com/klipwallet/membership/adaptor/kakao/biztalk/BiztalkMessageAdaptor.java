package com.klipwallet.membership.adaptor.kakao.biztalk;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.service.kakao.KakaoBizMessageService;
import com.klipwallet.membership.service.kakao.NotificationTalkMessage;

@Profile("!local")
@Component
@RequiredArgsConstructor
public class BiztalkMessageAdaptor implements KakaoBizMessageService {
    private final BgmsApiClient bgmsApiClient;

    /**
     * {@inheritDoc}
     * <p>Biztalk사의 BGMS를 통해서 알림톡 발송</p>
     *
     * @param phoneNumber 수신자 휴대폰 번호
     * @param message     발송 메시지
     */
    @Async
    @Override
    public void sendNotificationTalk(String phoneNumber, NotificationTalkMessage message) {
        // TODO @Jordan
    }
}
