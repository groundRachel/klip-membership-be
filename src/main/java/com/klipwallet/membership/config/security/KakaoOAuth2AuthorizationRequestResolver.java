package com.klipwallet.membership.config.security;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.klipwallet.membership.dto.OneTimeAction;

import static org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;

@Slf4j
public class KakaoOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
    private static final char PATH_DELIMITER = '/';

    private static final String REGISTRATION_ID_URI_VARIABLE_NAME = "registrationId";
    private static final StringKeyGenerator DEFAULT_STATE_GENERATOR = new Base64StringKeyGenerator(
            Base64.getUrlEncoder());
    private final OAuth2AuthorizationRequestResolver defaultImpl;
    private final ClientRegistrationRepository clientRegistrationRepository;

    private final AntPathRequestMatcher authorizationRequestMatcher;


    public KakaoOAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.defaultImpl =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
        this.authorizationRequestMatcher = new AntPathRequestMatcher(
                DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/{" + REGISTRATION_ID_URI_VARIABLE_NAME + "}");
    }

    private static String expandRedirectUri(HttpServletRequest request, ClientRegistration clientRegistration,
                                            String action) {
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("registrationId", clientRegistration.getRegistrationId());

        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
                                                          .replacePath(request.getContextPath())
                                                          .replaceQuery(null)
                                                          .fragment(null)
                                                          .build();

        String scheme = uriComponents.getScheme();
        uriVariables.put("baseScheme", (scheme != null) ? scheme : "");
        String host = uriComponents.getHost();
        uriVariables.put("baseHost", (host != null) ? host : "");
        // following logic is based on HierarchicalUriComponents#toUriString()
        int port = uriComponents.getPort();
        uriVariables.put("basePort", (port == -1) ? "" : ":" + port);
        String path = uriComponents.getPath();
        if (StringUtils.hasLength(path)) {
            if (path.charAt(0) != PATH_DELIMITER) {
                path = PATH_DELIMITER + path;
            }
        }
        uriVariables.put("basePath", (path != null) ? path : "");
        uriVariables.put("baseUrl", uriComponents.toUriString());
        uriVariables.put("action", (action != null) ? action : "");
        return UriComponentsBuilder.fromUriString(clientRegistration.getRedirectUri())
                                   .buildAndExpand(uriVariables)
                                   .toUriString();
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        String registrationId = resolveRegistrationId(request);
        if (registrationId == null) {
            return null;
        }
        if (isGoogle(registrationId)) {
            return defaultImpl.resolve(request);
        }
        String redirectUriAction = getRedirectUriAction(request, "login");
        OneTimeAction otAction = getOtAction(request);
        return resolve(request, registrationId, redirectUriAction, otAction);
    }

    private String resolveRegistrationId(HttpServletRequest request) {
        if (this.authorizationRequestMatcher.matches(request)) {
            return this.authorizationRequestMatcher.matcher(request).getVariables()
                                                   .get(REGISTRATION_ID_URI_VARIABLE_NAME);
        }
        return null;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        if (clientRegistrationId == null) {
            return null;
        }
        if (isGoogle(clientRegistrationId)) {
            return defaultImpl.resolve(request, clientRegistrationId);
        }
        String redirectUriAction = getRedirectUriAction(request, "authorize");
        OneTimeAction otAction = getOtAction(request);
        return resolve(request, clientRegistrationId, redirectUriAction, otAction);
    }

    @NonNull
    private OneTimeAction getOtAction(HttpServletRequest request) {
        String otActionString = request.getParameter("otAction");
        if (otActionString == null) {
            return OneTimeAction.NONE;
        }
        return OneTimeAction.fromDisplay(otActionString);
    }

    private String getRedirectUriAction(HttpServletRequest request, String defaultAction) {
        String action = request.getParameter("action");
        if (action == null) {
            return defaultAction;
        }
        return action;
    }

    private OAuth2AuthorizationRequest resolve(HttpServletRequest request, String registrationId,
                                               String redirectUriAction, OneTimeAction otAction) {
        if (registrationId == null) {
            return null;
        }
        ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            throw new IllegalArgumentException("Invalid Client Registration with Id: " + registrationId);
        }
        OAuth2AuthorizationRequest.Builder builder = getBuilder(clientRegistration);

        String redirectUriStr = expandRedirectUri(request, clientRegistration, redirectUriAction);
        String state = generateState(request, otAction);

        builder.clientId(clientRegistration.getClientId())
               .authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
               .redirectUri(redirectUriStr)
               .scopes(clientRegistration.getScopes())
               .state(state);
        // builder.additionalParameters(params -> params.put("prompt", "login"));
        if (isKakaoInAppBrowser(request)) {
            /*
             * 카카오 인톡 내 브라우저에서 인증 시 필요한 파라미터. UserAgent 헤더로 분기 처리가 필요하다.
             * https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-code-additional-consent
             * https://developers.kakao.com/docs/latest/ko/kakaologin/common#authentication-auto-login
             */
            log.info("isKakaoInAppBrowser: {}", request);
        }
        OAuth2AuthorizationRequest result = builder.build();
        log.info("clientId: {}\nredirectUri: {}\nscopes: {}\nstate: {}\nadditionalParameters: {}",
                 result.getClientId(), result.getRedirectUri(), result.getScopes(), result.getState(), result.getAdditionalParameters());
        return result;
    }

    private boolean isKakaoInAppBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        if (userAgent == null) {
            return false;
        }
        return userAgent.contains("KAKAOTALK");
    }

    private String generateState(HttpServletRequest request, OneTimeAction otAction) {
        String code = request.getParameter("code");
        if (code == null) {
            return "%s:%s".formatted(DEFAULT_STATE_GENERATOR.generateKey(), otAction.toDisplay());
        }
        return "%s:%s".formatted(code, otAction.toDisplay());
    }

    private OAuth2AuthorizationRequest.Builder getBuilder(ClientRegistration clientRegistration) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(clientRegistration.getAuthorizationGrantType())) {
            return OAuth2AuthorizationRequest.authorizationCode().attributes(
                    attrs -> attrs.put(OAuth2ParameterNames.REGISTRATION_ID, clientRegistration.getRegistrationId()));
        }
        throw new IllegalArgumentException(
                "Invalid Authorization Grant Type (" + clientRegistration.getAuthorizationGrantType().getValue()
                + ") for Client Registration with Id: " + clientRegistration.getRegistrationId());
    }

    private boolean isGoogle(String clientRegistrationId) {
        return clientRegistrationId.equals("google");
    }
}
