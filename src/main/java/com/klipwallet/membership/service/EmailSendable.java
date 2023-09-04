package com.klipwallet.membership.service;

import org.springframework.mail.SimpleMailMessage;

public interface EmailSendable {
    boolean sendEmail(SimpleMailMessage message);
}
