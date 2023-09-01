package com.klipwallet.membership.config;

import jakarta.validation.constraints.NotEmpty;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Value
@ConfigurationProperties(prefix = "cloud.aws.ses")
public class AwsSesProperties {
    String senderEmail;

    @ConstructorBinding
    public AwsSesProperties(@NotEmpty String senderEmail) {
        this.senderEmail = senderEmail;
    }
}
