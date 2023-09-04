package com.klipwallet.membership.config;

import jakarta.validation.constraints.NotEmpty;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Value
@ConfigurationProperties(prefix = "cloud.aws.s3")
@Validated
public class AwsS3Properties {
    @NotEmpty
    String bucket;
    @NotEmpty
    String prefix;

    @ConstructorBinding
    public AwsS3Properties(String bucket, String prefix) {
        this.bucket = bucket;
        this.prefix = prefix;
    }
}
