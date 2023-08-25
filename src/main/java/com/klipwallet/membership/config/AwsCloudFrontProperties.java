package com.klipwallet.membership.config;

import jakarta.validation.constraints.NotEmpty;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;


@Value
@ConfigurationProperties(prefix = "cloud.aws.cloudfront")
public class AwsCloudFrontProperties {
    String distributionDomain;

    @ConstructorBinding
    public AwsCloudFrontProperties(@NotEmpty String distributionDomain) {
        this.distributionDomain = distributionDomain;
    }
}
