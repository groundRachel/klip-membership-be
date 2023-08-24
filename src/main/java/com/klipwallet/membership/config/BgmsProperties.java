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

    @ConstructorBinding
    public BgmsProperties(String apiUrl, String id, String password) {
        this.apiUrl = apiUrl;
        this.id = id;
        this.password = password;
    }
}
