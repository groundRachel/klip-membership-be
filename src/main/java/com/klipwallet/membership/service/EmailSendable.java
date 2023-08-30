package com.klipwallet.membership.service;

import org.springframework.mail.SimpleMailMessage;

public interface EmailSendable {
    void sendEmail(SimpleMailMessage message);
}
