package com.klipwallet.membership.config.security;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import lombok.NonNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownContentTypeException;

import static com.klipwallet.membership.config.SecurityConfig.CLIENT_ID_KAKAO;
import static java.time.temporal.ChronoUnit.HOURS;

/**
 * 원래 OAuth2 Introspection 스팩은 AccessToken 을 Token Introspection Endpoint 를 통해서 조회한 후 해당 리소스 정보로 인증 되어야함.
 * 하지만 Kakao 로그인에서는 OAuth2 Introspection Endpoint를 공개하지 않아서 내부적으로 {@code user-info-uri}를 통해서 직접 조회한 후 해당 리소스를 반환하게 커스터마이징함.
 * 그래서 표준에 맞지 않는 스펙이기 때문에 추후 변경 시 주의가 필요함.
 *
 * @see <a href="https://www.oauth.com/oauth2-servers/token-introspection-endpoint/">Token Introspection Endpoint</a>
 */
public class KakaoOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    private static final String INVALID_USER_INFO_RESPONSE_ERROR_CODE = "invalid_user_info_response";
    @SuppressWarnings("Convert2Diamond")
    private static final ParameterizedTypeReference<Map<String, Object>> STRING_OBJECT_MAP = new ParameterizedTypeReference<Map<String, Object>>() {
    };

    private final RestOperations restOperations;
    private final Converter<String, RequestEntity<?>> requestEntityConverter;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public KakaoOpaqueTokenIntrospector(ClientRegistrationRepository clientRegistrationRepository) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        this.restOperations = restTemplate;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.requestEntityConverter =
                this.defaultRequestEntityConverter(URI.create(getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri()));
    }

    private Converter<String, RequestEntity<?>> defaultRequestEntityConverter(URI introspectionUri) {
        return (token) -> {
            HttpHeaders headers = requestHeaders(token);
            return new RequestEntity<>(headers, HttpMethod.GET, introspectionUri);
        };
    }

    private HttpHeaders requestHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED));
        return headers;
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        RequestEntity<?> requestEntity = this.requestEntityConverter.convert(token);
        if (requestEntity == null) {
            throw new OAuth2IntrospectionException("requestEntityConverter returned a null entity");
        }
        ResponseEntity<Map<String, Object>> responseEntity = makeRequest(requestEntity);
        return toKlipMemberOauth2User(responseEntity, token);
    }

    private ResponseEntity<Map<String, Object>> makeRequest(RequestEntity<?> request) {
        try {
            return this.restOperations.exchange(request, STRING_OBJECT_MAP);
        } catch (OAuth2AuthorizationException ex) {
            OAuth2Error oauth2Error = ex.getError();
            StringBuilder errorDetails = new StringBuilder();
            errorDetails.append("Error details: [");
            errorDetails.append("UserInfo Uri: ")
                        .append(getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri());
            errorDetails.append(", Error Code: ").append(oauth2Error.getErrorCode());
            if (oauth2Error.getDescription() != null) {
                errorDetails.append(", Error Description: ").append(oauth2Error.getDescription());
            }
            errorDetails.append("]");
            oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                                          "An error occurred while attempting to retrieve the UserInfo Resource: " + errorDetails,
                                          null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
        } catch (UnknownContentTypeException ex) {
            OAuth2Error oauth2Error = getoAuth2Error(ex);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
        } catch (RestClientException ex) {
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                                                      "An error occurred while attempting to retrieve the UserInfo Resource: " + ex.getMessage(),
                                                      null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
        }
    }

    @NonNull
    private OAuth2Error getoAuth2Error(UnknownContentTypeException ex) {
        String errorMessage = "An error occurred while attempting to retrieve the UserInfo Resource from '"
                              + getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri()
                              + "': response contains invalid content type '" + ex.getContentType() + "'. "
                              + "The UserInfo Response should return a JSON object (content type 'application/json') "
                              + "that contains a collection of name and value pairs of the claims about the authenticated End-User. "
                              + "Please ensure the UserInfo Uri in UserInfoEndpoint for Client Registration '"
                              + getClientRegistration().getRegistrationId() + "' conforms to the UserInfo Endpoint, "
                              + "as defined in OpenID Connect 1.0: 'https://openid.net/specs/openid-connect-core-1_0.html#UserInfo'";
        return new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE, errorMessage, null);
    }

    private KlipMembershipOAuth2User toKlipMemberOauth2User(ResponseEntity<Map<String, Object>> responseEntity, String token) {
        Map<String, Object> userAttributes = responseEntity.getBody();
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        authorities.add(new OAuth2UserAuthority(userAttributes));
        OAuth2AccessToken accessToken = new OAuth2AccessToken(TokenType.BEARER, token, Instant.now(), Instant.now().plus(1, HOURS));
        String nameAttributeKey =
                clientRegistrationRepository.findByRegistrationId(CLIENT_ID_KAKAO).getProviderDetails().getUserInfoEndpoint()
                                            .getUserNameAttributeName();
        DefaultOAuth2User oauth2User = new DefaultOAuth2User(authorities, Objects.requireNonNull(userAttributes), nameAttributeKey);
        return KlipMembershipOAuth2User.kakao(oauth2User, accessToken);
    }

    private ClientRegistration getClientRegistration() {
        return clientRegistrationRepository.findByRegistrationId(CLIENT_ID_KAKAO);
    }
}
