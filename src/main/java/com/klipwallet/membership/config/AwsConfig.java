package com.klipwallet.membership.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sesv2.SesV2Client;

@Configuration(proxyBeanMethods = false)
public class AwsConfig {
    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public S3Client amazonS3Client() {
        return S3Client.builder().region(Region.of(region)).build();
    }

    @Bean
    public SesV2Client amazonSesClient() {
        return SesV2Client.builder().region(Region.of(region)).build();
    }
}
