package com.klipwallet.membership.adaptor.ses;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.dto.email.EmailResult;
import com.klipwallet.membership.service.EmailNotifier;

@Primary
@Profile("!local")
@Component
@Slf4j
@RequiredArgsConstructor
public class SesAdaptor implements EmailNotifier {
    @Override
    public EmailResult sendEmail(SimpleMailMessage message) {
        // TODO SES
        return null;
    }
}
