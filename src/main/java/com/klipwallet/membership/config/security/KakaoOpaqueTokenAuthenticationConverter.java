package com.klipwallet.membership.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenAuthenticationConverter;

public class KakaoOpaqueTokenAuthenticationConverter implements OpaqueTokenAuthenticationConverter {
    @Override
    public Authentication convert(String introspectedToken, OAuth2AuthenticatedPrincipal authenticatedPrincipal) {
        return null;
    }
}
