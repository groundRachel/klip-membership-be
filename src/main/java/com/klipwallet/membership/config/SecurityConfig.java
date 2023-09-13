package com.klipwallet.membership.config;

import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DelegatingOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.klipwallet.membership.config.security.KakaoBearerTokenResolver;
import com.klipwallet.membership.config.security.KakaoOAuth2AuthorizationRequestResolver;
import com.klipwallet.membership.config.security.KakaoOAuth2UserService;
import com.klipwallet.membership.config.security.KakaoOpaqueTokenIntrospector;
import com.klipwallet.membership.config.security.KlipMembershipOAuth2SuccessHandler;
import com.klipwallet.membership.config.security.KlipMembershipOAuth2UserService;
import com.klipwallet.membership.config.security.ProblemDetailEntryPoint;
import com.klipwallet.membership.service.AdminService;
import com.klipwallet.membership.service.OperatorInvitable;
import com.klipwallet.membership.service.PartnerService;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@SuppressWarnings("Convert2MethodRef")
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@ConditionalOnWebApplication
public class SecurityConfig {
    /**
     * <pre>spring.security.oauth2.client.registration."google"</pre>
     */
    public static final String CLIENT_ID_GOOGLE = "google";
    /**
     * <pre>spring.security.oauth2.client.registration."kakao"</pre>
     */
    public static final String CLIENT_ID_KAKAO = "kakao";
    public static final String OAUTH2_USER = "OAUTH2_USER";
    public static final String KLIP_KAKAO = "KLIP_KAKAO";
    public static final String PARTNER = "PARTNER";
    public static final String ADMIN = "ADMIN";
    public static final String SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String ROLE_KLIP_KAKAO = ROLE_PREFIX + KLIP_KAKAO;
    public static final String ROLE_ADMIN = ROLE_PREFIX + ADMIN;
    public static final String ROLE_SUPER_ADMIN = ROLE_PREFIX + SUPER_ADMIN;
    public static final String ROLE_PARTNER = ROLE_PREFIX + PARTNER;

    /**
     * {@code local} 환경에서 {@code /h2-console} 접근을 위한 보안 약화 설정
     */
    @Order(-1)
    @Profile("local")
    @Bean
    public SecurityFilterChain localFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher(antMatcher("/h2-console/**")) // /h2-console
            .csrf(c -> c.disable())
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))
            .authorizeHttpRequests(
                    a -> a.requestMatchers(antMatcher("/h2-console/**")).permitAll())
            .requestCache(r -> r.disable())
            .securityContext(s -> s.disable())
            .sessionManagement(s -> s.disable())
            .formLogin(f -> f.disable())
            .httpBasic(h -> h.disable())
            .anonymous(a -> a.disable())
            .logout(l -> l.disable())
            .rememberMe(r -> r.disable());

        return http.build();
    }

    @Profile({"local", "local-dev", "dev"})
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                         .requestMatchers(antMatcher("/login.html"));
    }

    /**
     * authorizeHttpRequests 설정 시 주의해야할 사항이 있음.
     * <p>requestMatchers 순서대로 권한검사를 하니 세사한 권한이 먼저 정의되어야함.</p>
     *
     * @param http                                     HttpSecurity
     * @param customAuthenticationEntryPoint           커스텀 인증 예외(401) 시 인증을 시작하는 Entry
     * @param customOauth2UserService                  OAuth2User 를 조회하기 위한 서비스
     * @param customOAuth2AuthorizationRequestResolver OAuth2 Authorization 요청(code -> AccessToken) 시 변조를 위한 컴포넌트
     * @param customOAuth2SuccessHandler               OAuth2 인증 성공 후 후속 처리를 위한 핸들러(구글, 카카오 OAuth2 별 분기 처리)
     * @param customCorsConfigurationSource            CORS 설정
     * @param kakaoOpaqueTokenIntrospector             카카오 토큰을 spring-security resource-server 기능으로 인증 하고자 사용. OAuth2 Introspection 상당 부분 커스터마이징 해서 Kakao 계정 정보를 조회하는데 사용함.
     * @param kakaoBearerTokenResolver                 카카오 토큰을 Bearer가 아니라 Kakao 로 받아서 처리하기 위한 커스터마이징 토큰 리졸버
     * @param kakaoOpaqueTokenAuthenticationConverter  Bearer 토큰 인증을 OAuth2인증 토큰으로 변환하기 위한 컨버터 (추후 Session으로 설정하고자 함)
     * @param defaultSecurityContextRepository         사용자 정의 SecurityContextRepository (Session + RequestAttribute)
     */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationEntryPoint customAuthenticationEntryPoint,
                                                   OAuth2UserService<OAuth2UserRequest, OAuth2User> customOauth2UserService,
                                                   OAuth2AuthorizationRequestResolver customOAuth2AuthorizationRequestResolver,
                                                   AuthenticationSuccessHandler customOAuth2SuccessHandler,
                                                   CorsConfigurationSource customCorsConfigurationSource,
                                                   KakaoOpaqueTokenIntrospector kakaoOpaqueTokenIntrospector,
                                                   KakaoBearerTokenResolver kakaoBearerTokenResolver,
                                                   OpaqueTokenAuthenticationConverter kakaoOpaqueTokenAuthenticationConverter,
                                                   SecurityContextRepository defaultSecurityContextRepository) throws Exception {
        http.authorizeHttpRequests(
                    a -> a.requestMatchers(antMatcher("/tool/v1/faqs/**"),
                                           antMatcher("/tool/v1/members/me")).permitAll()
                          .requestMatchers(antMatcher("/tool/v1/partner-applications/**")).hasAuthority(OAUTH2_USER)
                          .requestMatchers(antMatcher("/tool/v1/**")).hasRole(PARTNER)
                          .requestMatchers(antMatcher("/admin/v1/members/me")).permitAll()
                          .requestMatchers(antMatcher("/admin/v1/admins/**")).hasRole(SUPER_ADMIN)
                          .requestMatchers(antMatcher("/admin/v1/**")).hasRole(ADMIN)
                          .requestMatchers(antMatcher("/external/v1/operators")).hasRole(KLIP_KAKAO)
                          .requestMatchers(antMatcher("/external/v1/open-chattings/**")).hasRole(KLIP_KAKAO)
                          .requestMatchers(antMatcher("/external/v1/**")).permitAll()
                          .requestMatchers(antMatcher("/error/**")).permitAll()
                          .requestMatchers(antMatcher("/actuator/**")).permitAll()      // actuator
                          .requestMatchers(antMatcher("/swagger-ui/**"),
                                           antMatcher("/v3/api-docs/**")).permitAll()   // for swagger
                          .requestMatchers(antMatcher("/internal/v1/user")).permitAll()
                          .requestMatchers(antMatcher("/")).permitAll()
                          .anyRequest().authenticated())
            .oauth2Login(
                    o -> o.successHandler(customOAuth2SuccessHandler)
                          .authorizationEndpoint(
                                  a -> a.authorizationRequestResolver(customOAuth2AuthorizationRequestResolver))
                          .userInfoEndpoint(
                                  u -> u.userService(customOauth2UserService)))
            .oauth2ResourceServer(
                    o -> o.bearerTokenResolver(kakaoBearerTokenResolver)
                          .opaqueToken(
                                  t -> t.introspector(kakaoOpaqueTokenIntrospector)
                                        .authenticationConverter(kakaoOpaqueTokenAuthenticationConverter))
                          .addObjectPostProcessor(bearerTokenAuthenticationFilterPostProcessor(defaultSecurityContextRepository)))
            .cors(c -> c.configurationSource(customCorsConfigurationSource))
            .csrf(c -> c.disable())
            .requestCache(c -> c.disable())
            .anonymous(a -> a.disable())
            .httpBasic(h -> h.disable())
            .formLogin(f -> f.disable())
            .rememberMe(r -> r.disable())
            .exceptionHandling(
                    e -> e.accessDeniedPage("/error/403")
                          .authenticationEntryPoint(customAuthenticationEntryPoint)
            );
        return http.build();
    }

    @NonNull
    private ObjectPostProcessor<BearerTokenAuthenticationFilter> bearerTokenAuthenticationFilterPostProcessor(
            SecurityContextRepository defaultSecurityContextRepository) {
        return new ObjectPostProcessor<>() {
            @Override
            public <O extends BearerTokenAuthenticationFilter> O postProcess(O object) {
                object.setSecurityContextRepository(defaultSecurityContextRepository);
                return object;
            }
        };
    }

    /**
     * Implementation of OAuth2UserService
     */
    @Primary
    @Bean
    DelegatingOAuth2UserService<OAuth2UserRequest, OAuth2User> customOauth2UserService(PartnerService partnerService,
                                                                                       AdminService adminService) {
        var klipMembershipOAuth2UserService = new KlipMembershipOAuth2UserService(partnerService, adminService);
        var kakaoOAuth2UserService = new KakaoOAuth2UserService();
        return new DelegatingOAuth2UserService<>(List.of(klipMembershipOAuth2UserService, kakaoOAuth2UserService));
    }

    @Bean
    KlipMembershipOAuth2SuccessHandler customOAuth2SuccessHandler(KlipMembershipProperties properties, OperatorInvitable operatorInvitable) {
        return new KlipMembershipOAuth2SuccessHandler(properties, operatorInvitable);
    }

    @Bean
    KakaoOAuth2AuthorizationRequestResolver kakaoOAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        return new KakaoOAuth2AuthorizationRequestResolver(clientRegistrationRepository);
    }

    @Bean
    CorsConfiguration baseCorsConfiguration(KlipMembershipProperties properties) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of(properties.getToolFrontUrl(), properties.getAdminFrontUrl()));
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        return configuration;
    }

    @SuppressWarnings("HttpUrlsUsage")
    @Profile({"local", "local-dev", "dev"})
    @Primary
    @Bean
    CorsConfigurationSource localCorsConfigurationSource(CorsConfiguration baseCorsConfiguration) {
        baseCorsConfiguration.addAllowedOrigin("http://localhost:3000");
        baseCorsConfiguration.addAllowedOrigin("http://127.0.0.1:3000");
        baseCorsConfiguration.addAllowedOrigin("http://membership.local.klipwallet.com:3000");
        baseCorsConfiguration.addAllowedOrigin("http://membership-admin.local.klipwallet.com:3000");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", baseCorsConfiguration);
        return source;
    }

    @Bean
    SecurityContextRepository defaultSecurityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository());
    }

    @Profile({"!local & !local-dev & !dev"})
    @Primary
    @Bean
    CorsConfigurationSource defaultCorsConfigurationSource(CorsConfiguration baseCorsConfiguration) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", baseCorsConfiguration);
        return source;
    }

    @Bean
    KakaoOpaqueTokenIntrospector kakaoOpaqueTokenIntrospector(ClientRegistrationRepository clientRegistrationRepository) {
        return new KakaoOpaqueTokenIntrospector(clientRegistrationRepository);
    }

    @Bean
    KakaoBearerTokenResolver kakaoBearerTokenResolver() {
        return new KakaoBearerTokenResolver();
    }

    @Bean
    OpaqueTokenAuthenticationConverter kakaoOpaqueTokenAuthenticationConverter() {
        return (token, authenticatedPrincipal) ->
                       new OAuth2AuthenticationToken((OAuth2User) authenticatedPrincipal, authenticatedPrincipal.getAuthorities(), CLIENT_ID_KAKAO);
    }

    /**
     * 인증 실패 시 처리하는 {@link AuthenticationEntryPointConfig} 구성
     * <div>
     * <b>case 1. Local 환경인 경우:</b>
     * <p>
     * delegatingAuthenticationEntryPoint = loginUrlAuthenticationEntryPoint(text/html) + problemDetailEntryPoint(application/json, default)
     * 로 구성됨
     * </p>
     * <b>case 2. 그 외 환경인 경우</b>
     * <p>
     * problemDetailEntryPoint 단독으로 구성
     * </p>
     * </div>
     */
    @Configuration(proxyBeanMethods = false)
    public static class AuthenticationEntryPointConfig {
        @NonNull
        private static MediaTypeRequestMatcher mediaTypeMatcher(MediaType... mediaTypes) {
            MediaTypeRequestMatcher requestMatcher = new MediaTypeRequestMatcher(mediaTypes);
            requestMatcher.setUseEquals(true);
            return requestMatcher;
        }

        @Bean
        ProblemDetailEntryPoint problemDetailEntryPoint(ObjectMapper objectMapper, MessageSource messageSource) {
            return new ProblemDetailEntryPoint(objectMapper, messageSource);
        }

        @Profile("local")
        @Primary
        @Bean
        DelegatingAuthenticationEntryPoint delegatingAuthenticationEntryPoint(ProblemDetailEntryPoint problemDetailEntryPoint,
                                                                              LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint) {
            LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>(2);
            entryPoints.put(mediaTypeMatcher(MediaType.APPLICATION_JSON), problemDetailEntryPoint);
            entryPoints.put(mediaTypeMatcher(MediaType.TEXT_HTML), loginUrlAuthenticationEntryPoint);
            DelegatingAuthenticationEntryPoint bean = new DelegatingAuthenticationEntryPoint(entryPoints);
            bean.setDefaultEntryPoint(problemDetailEntryPoint);
            return bean;
        }

        @Profile("local")
        @Bean
        LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint() {
            return new LoginUrlAuthenticationEntryPoint("/login.html");
        }
    }
}
