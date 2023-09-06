package com.klipwallet.membership.adaptor.kas.feign;

import org.springframework.context.annotation.Bean;

import com.klipwallet.membership.config.KasApiProperties;

public class KasFeignConfig {
    @Bean
    KasRequestInterceptor kasRequestInterceptor(KasApiProperties kasApiProperties) {
        return new KasRequestInterceptor(kasApiProperties);
    }
}
