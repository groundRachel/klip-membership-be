package com.klipwallet.membership.adaptor.kas.feign;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

import com.klipwallet.membership.adaptor.kas.dto.KasError;
import com.klipwallet.membership.config.KasApiProperties;

public class KasFeignConfig {

    @Bean
    KasRequestInterceptor kasRequestInterceptor(KasApiProperties kasApiProperties) {
        return new KasRequestInterceptor(kasApiProperties);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new KasErrorDecoder();
    }

    public class KasErrorDecoder implements ErrorDecoder {
        @Override
        public Exception decode(String methodKey, Response response) {
            try {
                KasError kasError = decodeKasError(response.body().asInputStream());
                return kasError.convertException(response);
            } catch (Exception e) {
                return new RuntimeException("Error decoding KasError", e);
            }
        }

        private KasError decodeKasError(InputStream inputStream) throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(inputStream, KasError.class);
        }
    }
}
