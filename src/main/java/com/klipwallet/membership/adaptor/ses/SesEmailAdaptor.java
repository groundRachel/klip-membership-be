package com.klipwallet.membership.adaptor.ses;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.service.EmailSendable;

@Primary
@Profile("!local")
@Component
@Slf4j
@RequiredArgsConstructor
public class SesEmailAdaptor implements EmailSendable {
    @Override
    public void sendEmail(SimpleMailMessage message) {
        // TODO SES
    }
}
