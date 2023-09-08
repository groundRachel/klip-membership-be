package com.klipwallet.membership.adaptor.kas.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.http.HttpHeaders;

import com.klipwallet.membership.config.KasApiProperties;

public class KasRequestInterceptor implements RequestInterceptor {
    private static final String HEADER_CHAIN_ID = "x-chain-id";
    private final String chainId;
    private final String authorizationValue;

    public KasRequestInterceptor(KasApiProperties kasApiProperties) {
        this.authorizationValue = kasApiProperties.getAuthorization();
        this.chainId = kasApiProperties.getChainId();
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header(HttpHeaders.AUTHORIZATION, authorizationValue);
        template.header(HEADER_CHAIN_ID, chainId);
    }
}
