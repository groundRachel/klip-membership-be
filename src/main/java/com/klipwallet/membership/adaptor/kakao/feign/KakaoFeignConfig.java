package com.klipwallet.membership.adaptor.kakao.feign;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

import com.klipwallet.membership.adaptor.kakao.dto.KakaoError;
import com.klipwallet.membership.config.KakaoApiProperties;

public class KakaoFeignConfig {
    @Bean
    KakaoRequestInterceptor kakaoRequestInterceptor(KakaoApiProperties kakaoApiProperties) {
        return new KakaoRequestInterceptor(kakaoApiProperties);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new KakaoErrorDecoder();
    }

    public class KakaoErrorDecoder implements ErrorDecoder {
        @Override
        public Exception decode(String methodKey, Response response) {
            try {
                KakaoError kakaoError = decodeKakaoError(response.body().asInputStream());
                return kakaoError.convertException(response);
            } catch (Exception e) {
                return new RuntimeException("Error decoding KakaoError", e);
            }
        }

        private KakaoError decodeKakaoError(InputStream inputStream) throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(inputStream, KakaoError.class);
        }
    }
}
