package com.klipwallet.membership.config;

import jakarta.validation.constraints.NotNull;

import lombok.Value;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Value
@Validated
@ConfigurationProperties(prefix = "application.klip-membership")
public class KlipMembershipProperties {
    @NotNull @URL
    String toolFrontUrl;

    @NotNull @URL
    String adminFrontUrl;

    @ConstructorBinding
    public KlipMembershipProperties(String toolFrontUrl, String adminFrontUrl) {
        this.toolFrontUrl = toolFrontUrl;
        this.adminFrontUrl = adminFrontUrl;
    }
}
