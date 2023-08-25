package com.klipwallet.membership.adaptor.kakao.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.http.HttpHeaders;

import com.klipwallet.membership.config.KakaoApiProperties;

public class KakaoRequestInterceptor implements RequestInterceptor {
    private final String authorizationValue;

    public KakaoRequestInterceptor(KakaoApiProperties kakaoApiProperties) {
        this.authorizationValue = "KakaoAK %s".formatted(kakaoApiProperties.getAdminKey());
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header(HttpHeaders.AUTHORIZATION, authorizationValue);
    }
}
