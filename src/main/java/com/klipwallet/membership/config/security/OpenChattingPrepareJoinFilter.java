package com.klipwallet.membership.config.security;

import java.io.IOException;
import java.time.Instant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import static com.klipwallet.membership.config.SecurityConfig.CLIENT_ID_KAKAO;
import static java.time.temporal.ChronoUnit.HOURS;

/**
 * @deprecated 현재 해당 필터를 이용하지 않고 OAuth2ResourceServer 과 OpaqueTokenInstrospector를 커스터마이징해서 구현했음.
 * 해당 필터는 이력 상 우선 Commit이 필요해서 남겨둠
 */
@Deprecated
public class OpenChattingPrepareJoinFilter extends AbstractAuthenticationProcessingFilter {
    private static final String DEFAULT_FILTER_PROCESSES_URI = "/login/oauth2/code/*";

    private static final String CLIENT_REGISTRATION_NOT_FOUND_ERROR_CODE = "client_registration_not_found";

    private final ClientRegistrationRepository clientRegistrationRepository;

    private final OAuth2AuthorizedClientRepository authorizedClientRepository;

    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository =
            new HttpSessionOAuth2AuthorizationRequestRepository();

    public OpenChattingPrepareJoinFilter(ClientRegistrationRepository clientRegistrationRepository,
                                         OAuth2AuthorizedClientService authorizedClientService) {
        this(clientRegistrationRepository, authorizedClientService, DEFAULT_FILTER_PROCESSES_URI);
    }

    private OpenChattingPrepareJoinFilter(ClientRegistrationRepository clientRegistrationRepository,
                                          OAuth2AuthorizedClientService authorizedClientService, String filterProcessesUrl) {
        this(clientRegistrationRepository,
             new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService),
             filterProcessesUrl);
    }

    private OpenChattingPrepareJoinFilter(ClientRegistrationRepository clientRegistrationRepository,
                                          OAuth2AuthorizedClientRepository authorizedClientRepository, String filterProcessesUrl) {
        super(filterProcessesUrl);
        Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository cannot be null");
        Assert.notNull(authorizedClientRepository, "authorizedClientRepository cannot be null");
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authorizedClientRepository = authorizedClientRepository;
    }

    static String getKakaoAccessToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null) {
            return null;
        }
        if (!authorization.startsWith("Kakao ")) {
            return null;
        }
        return authorization.split(" ")[1];
    }

    static OAuth2AuthorizationResponse convert(String redirectUri) {
        return OAuth2AuthorizationResponse.success("{kakao.code}").redirectUri(redirectUri).build();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        String kakaoAccessToken = getKakaoAccessToken(request);
        if (kakaoAccessToken == null) {
            throw new AuthenticationCredentialsNotFoundException("Not found Kakao AccessToken");
        }

        String registrationId = CLIENT_ID_KAKAO;
        ClientRegistration clientRegistration =
                this.clientRegistrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            OAuth2Error oauth2Error = new OAuth2Error(CLIENT_REGISTRATION_NOT_FOUND_ERROR_CODE,
                                                      "Client Registration not found with Id: " + registrationId, null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        OAuth2AuthorizationRequest authorizationRequest =
                OAuth2AuthorizationRequest.authorizationCode()
                                          .authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
                                          .clientId(clientRegistration.getClientId())
                                          .build();
        String redirectUri = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
                                                 .replaceQuery(null)
                                                 .build()
                                                 .toUriString();
        // @formatter:on
        OAuth2AuthorizationResponse authorizationResponse = convert(redirectUri);

        Object authenticationDetails = this.authenticationDetailsSource.buildDetails(request);
        OAuth2AuthorizationExchange authorizationExchange = new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse);

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                TokenType.BEARER, kakaoAccessToken, Instant.now(), Instant.now().plus(1, HOURS), clientRegistration.getScopes());
        OAuth2AuthorizationCodeAuthenticationToken oauth2Authentication = new OAuth2AuthorizationCodeAuthenticationToken(
                clientRegistration, authorizationExchange, accessToken);
        oauth2Authentication.setDetails(authenticationDetails);

        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(
                oauth2Authentication.getClientRegistration(), oauth2Authentication.getName(),
                oauth2Authentication.getAccessToken(), oauth2Authentication.getRefreshToken());

        this.authorizedClientRepository.saveAuthorizedClient(authorizedClient, oauth2Authentication, request, response);
        return oauth2Authentication;
    }
}
