package com.klipwallet.membership.adaptor.ses;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.mail.SimpleMailMessage;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Disabled("직접 이메일 전송하는 테스트이기 때문에 Disabled")
class SesEmailAdaptorTest {
    @Autowired
    SesEmailAdaptor sesEmailAdaptor;

    @Test
    void sendEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("winnie.byun+1@groundx.xyz");
        message.setSubject("[KMT 이메일 전송 테스트] 이메일 제목");
        message.setText("[KMT 이메일 전송 테스트] 이메일 내용입니다.");

        sesEmailAdaptor.sendEmail(message);
    }
}
