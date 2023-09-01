package com.klipwallet.membership.config;

import jakarta.validation.constraints.NotEmpty;

import lombok.Value;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "application.klip-drops-internal")
@Validated
@Value
public class KlipDropsInternalProperties {
    @NotEmpty @URL
    String apiUrl;

    @ConstructorBinding
    public KlipDropsInternalProperties(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
