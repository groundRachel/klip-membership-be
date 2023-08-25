package com.klipwallet.membership.adaptor.kakao.feign;

import org.springframework.context.annotation.Bean;

import com.klipwallet.membership.config.KakaoApiProperties;

public class KakaoFeignConfig {
    @Bean
    KakaoRequestInterceptor kakaoRequestInterceptor(KakaoApiProperties kakaoApiProperties) {
        return new KakaoRequestInterceptor(kakaoApiProperties);
    }
}
