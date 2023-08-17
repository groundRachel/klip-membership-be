package com.klipwallet.membership.config;

import java.net.URL;

import jakarta.validation.constraints.NotEmpty;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Value
@ConfigurationProperties(prefix = "application.kakao-api")
public class KakaoApiProperties {
    String adminKey;
    Long domainId;
    URL openlinkUrl;

    @ConstructorBinding
    public KakaoApiProperties(@NotEmpty String adminKey, @NotEmpty Long domainId,
                              @NotEmpty @org.hibernate.validator.constraints.URL URL openlinkUrl) {
        this.adminKey = adminKey;
        this.domainId = domainId;
        this.openlinkUrl = openlinkUrl;
    }
}
