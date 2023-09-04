package com.klipwallet.membership.config;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Value
@ConfigurationProperties(prefix = "application.email")
@Validated
public class EmailProperties {
    @NotEmpty @Email
    String senderEmail;

    @ConstructorBinding
    public EmailProperties(String senderEmail) {
        this.senderEmail = senderEmail;
    }
}
