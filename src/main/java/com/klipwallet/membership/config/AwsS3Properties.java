package com.klipwallet.membership.config;

import jakarta.validation.constraints.NotEmpty;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Value
@ConfigurationProperties(prefix = "cloud.aws.s3")
public class AwsS3Properties {
    String bucket;
    String prefix;

    @ConstructorBinding
    public AwsS3Properties(@NotEmpty String bucket, @NotEmpty String prefix) {
        this.bucket = bucket;
        this.prefix = prefix;
    }
}
