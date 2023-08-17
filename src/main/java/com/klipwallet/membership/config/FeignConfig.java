package com.klipwallet.membership.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KakaoApiProperties.class)
public class FeignConfig {
    private final KakaoApiProperties kakaoApiProperties;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Authorization", "KakaoAK %s".formatted(kakaoApiProperties.getAdminKey()));
        };
    }
}
