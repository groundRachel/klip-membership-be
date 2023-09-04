package com.klipwallet.membership.config;

import jakarta.validation.constraints.NotEmpty;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "application.nft")
@Value
@Validated
public class NftProperties {
    /**
     * Drops NFT SCA
     */
    @NotEmpty String sca;

    @ConstructorBinding
    public NftProperties(String sca) {
        this.sca = sca;
    }
}
