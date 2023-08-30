package com.klipwallet.membership.service;

import org.springframework.mail.SimpleMailMessage;

import com.klipwallet.membership.dto.email.EmailResult;

public interface EmailNotifier {
    EmailResult sendEmail(SimpleMailMessage message);
}
