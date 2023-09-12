package com.klipwallet.membership.config.security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrors;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.util.StringUtils;

/**
 * 카카오 AccessToken 전용으로 사용할 것이라서 Bearer이 아닌 아래와 같은 {@code Authorization}을 파싱하게 한다.
 * <pre>Authorization: Kakao {AccessToken}</pre>
 */
public class KakaoBearerTokenResolver implements BearerTokenResolver {
    private static final Pattern authorizationPattern = Pattern.compile("^Kakao (?<token>[a-zA-Z0-9-._~+/]+=*)$",
                                                                        Pattern.CASE_INSENSITIVE);

    @Override
    public String resolve(final HttpServletRequest request) {
        return resolveFromAuthorizationHeader(request);
    }

    private String resolveFromAuthorizationHeader(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.startsWithIgnoreCase(authorization, "kakao")) {
            return null;
        }
        Matcher matcher = authorizationPattern.matcher(authorization);
        if (!matcher.matches()) {
            BearerTokenError error = BearerTokenErrors.invalidToken("Kakao token is malformed");
            throw new OAuth2AuthenticationException(error);
        }
        return matcher.group("token");
    }

}
