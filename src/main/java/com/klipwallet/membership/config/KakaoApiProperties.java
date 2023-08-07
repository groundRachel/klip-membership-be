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
    String userIdType;
    String testUserId;
    String testParticipantId;
    @ConstructorBinding
    public KakaoApiProperties(@NotEmpty String adminKey, @NotEmpty Long domainId,
                              @NotEmpty String userIdType, @NotEmpty @org.hibernate.validator.constraints.URL URL openlinkUrl,
                              String testUserId, String testParticipantId) {
        this.adminKey = adminKey;
        this.domainId = domainId;
        this.userIdType = userIdType;
        this.openlinkUrl = openlinkUrl;
        this.testUserId = testUserId;
        this.testParticipantId = testParticipantId;

    }
}
