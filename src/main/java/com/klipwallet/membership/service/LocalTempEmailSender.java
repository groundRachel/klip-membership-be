package com.klipwallet.membership.service;

import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

/**
 * Local {@link EmailSendable}
 */
@Profile("local")
@Component
@Slf4j
public class LocalTempEmailSender implements EmailSendable {

    @Override
    public boolean sendEmail(SimpleMailMessage message) {
        log.info("""
                 [Email] Successfully sent an email
                 from : {}
                 to : {}
                 title : {}
                 content : {}
                 """,
                 message.getFrom(), Objects.requireNonNull(message.getTo())[0], message.getSubject(), message.getText());
        return true;
    }
}
