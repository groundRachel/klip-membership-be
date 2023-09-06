package com.klipwallet.membership.adaptor.local;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.service.kakao.KakaoBizMessageService;
import com.klipwallet.membership.service.kakao.NotificationTalkMessage;

@Profile("local")
@Slf4j
@Component
public class DummyKakaoBizMessageService implements KakaoBizMessageService {
    @Override
    public void sendNotificationTalk(String phoneNumber, NotificationTalkMessage message) {
        log.info("phone: {}\n{}", phoneNumber, message);
    }
}
