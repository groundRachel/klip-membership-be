package com.klipwallet.membership.config;

import jakarta.validation.constraints.NotEmpty;

import lombok.Value;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "application.bgms")
@Validated
@Value
public class BgmsProperties {
    @NotEmpty @URL
    String apiUrl;
    /**
     * 비즈톡에서 발급한 ID(bsid)
     */
    @NotEmpty
    String id;
    /**
     * 비즈톡에서 발급한 PW(passwd)
     */
    @NotEmpty
    String password;
    /**
     * Sender Key
     */
    @NotEmpty
    String senderKey;
    /**
     * 운영진 초대 BGMS 템플릿 코드
     */
    @NotEmpty
    String inviteOperatorTemplateCode;

    @ConstructorBinding
    public BgmsProperties(String apiUrl, String id, String password, String senderKey, String inviteOperatorTemplateCode) {
        this.apiUrl = apiUrl;
        this.id = id;
        this.password = password;
        this.senderKey = senderKey;
        this.inviteOperatorTemplateCode = inviteOperatorTemplateCode;
    }
}
