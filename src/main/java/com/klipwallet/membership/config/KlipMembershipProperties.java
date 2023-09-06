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
    /**
     * 운영진 초대 Path
     */
    private static final String PATH_INVITE_OPERATOR = "/landing/invite-operator";
    @NotNull
    DeployEnv env;
    /**
     * Klip Membership Tool Origin
     * <p>ex: {@code https://membership.klipwallet.com}</p>
     */
    @NotNull @URL
    String toolFrontUrl;
    /**
     * Klip Membership Admin Origin
     * <p>ex: {@code https://membership-admin.klipwallet.com}</p>
     */
    @NotNull @URL
    String adminFrontUrl;


    @ConstructorBinding
    public KlipMembershipProperties(DeployEnv env, String toolFrontUrl, String adminFrontUrl) {
        this.env = env;
        this.toolFrontUrl = toolFrontUrl;
        this.adminFrontUrl = adminFrontUrl;
    }

    public String getInviteOperatorUrl() {
        return this.toolFrontUrl + PATH_INVITE_OPERATOR;
    }
}
