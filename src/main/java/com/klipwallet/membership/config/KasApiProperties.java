package com.klipwallet.membership.config;

import jakarta.validation.constraints.NotEmpty;

import lombok.Value;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Value
@ConfigurationProperties(prefix = "application.kas-api")
@Validated
public class KasApiProperties {
    @NotEmpty String authorization;
    @NotEmpty String chainId;
    @NotEmpty @URL String thUrl;

    @ConstructorBinding
    public KasApiProperties(String authorization, String chainId, String thUrl) {
        this.authorization = authorization;
        this.chainId = chainId;
        this.thUrl = thUrl;
    }
}
