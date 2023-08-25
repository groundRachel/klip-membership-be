package com.klipwallet.membership.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Value;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Value
@ConfigurationProperties(prefix = "application.kakao-api")
@Validated
public class KakaoApiProperties {
    @NotEmpty String adminKey;
    @NotNull Long domainId;
    @NotEmpty @URL String openlinkUrl;

    @ConstructorBinding
    public KakaoApiProperties(String adminKey, Long domainId, String openlinkUrl) {
        this.adminKey = adminKey;
        this.domainId = domainId;
        this.openlinkUrl = openlinkUrl;
    }
}
