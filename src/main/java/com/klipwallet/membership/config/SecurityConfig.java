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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DelegatingOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.klipwallet.membership.config.security.KakaoOAuth2AuthorizationRequestResolver;
import com.klipwallet.membership.config.security.KakaoOAuth2UserService;
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
    public static final String OAUTH2_USER = "OAUTH2_USER";
    public static final String KLIP_KAKAO = "KLIP_KAKAO";
    public static final String PARTNER = "PARTNER";
    public static final String ADMIN = "ADMIN";
    public static final String SUPER_ADMIN = "SUPERADMIN";
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
     */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationEntryPoint customAuthenticationEntryPoint,
                                                   OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService,
                                                   OAuth2AuthorizationRequestResolver customOAuth2AuthorizationRequestResolver,
                                                   AuthenticationSuccessHandler customOAuth2SuccessHandler,
                                                   CorsConfigurationSource customCorsConfigurationSource) throws Exception {
        http.csrf(c -> c.disable())
            .authorizeHttpRequests(
                    a -> a.requestMatchers(antMatcher("/tool/v1/faqs/**"),
                                           antMatcher("/tool/v1/members/me")).permitAll()
                          .requestMatchers(antMatcher("/tool/v1/partner-applications/**")).hasAuthority(OAUTH2_USER)
                          .requestMatchers(antMatcher("/tool/v1/**")).hasRole(PARTNER)
                          .requestMatchers(antMatcher("/admin/v1/admins/**")).hasRole(SUPER_ADMIN)
                          .requestMatchers(antMatcher("/admin/v1/**")).hasRole(ADMIN)
                          .requestMatchers(antMatcher("/external/v1/operators")).hasRole(KLIP_KAKAO)
                          .requestMatchers(antMatcher("/external/v1/**")).permitAll()
                          .requestMatchers(antMatcher("/error/**")).permitAll()
                          .requestMatchers(antMatcher("/actuator/**")).permitAll()      // actuator
                          .requestMatchers(antMatcher("/swagger-ui/**"),
                                           antMatcher("/v3/api-docs/**")).permitAll()   // for swagger
                          .requestMatchers(antMatcher("/oauth")).hasAuthority(OAUTH2_USER)
                          .requestMatchers(antMatcher("/user"),
                                           antMatcher("/usera"),
                                           antMatcher("/usero")).permitAll()
                          .anyRequest().authenticated())
            .oauth2Login(
                    o -> o.successHandler(customOAuth2SuccessHandler)
                          .authorizationEndpoint(
                                  a -> a.authorizationRequestResolver(customOAuth2AuthorizationRequestResolver))
                          .userInfoEndpoint(
                                  u -> u.userService(oauth2UserService)))
            .cors(c -> c.configurationSource(customCorsConfigurationSource))
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

    /**
     * Implementation of OAuth2UserService
     */
    @Primary
    @Bean
    DelegatingOAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService(PartnerService partnerService,
                                                                                 AdminService adminService) {
        var klipMembershipOAuth2UserService = new KlipMembershipOAuth2UserService(partnerService, adminService);
        var kakaoOAuth2UserService = new KakaoOAuth2UserService();
        return new DelegatingOAuth2UserService<>(List.of(klipMembershipOAuth2UserService, kakaoOAuth2UserService));
    }

    @Bean
    KlipMembershipOAuth2SuccessHandler kakaoOAuth2SuccessHandler(KlipMembershipProperties properties, OperatorInvitable operatorInvitable) {
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

    @Profile({"!local & !local-dev & !dev"})
    @Primary
    @Bean
    CorsConfigurationSource defaultCorsConfigurationSource(CorsConfiguration baseCorsConfiguration) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", baseCorsConfiguration);
        return source;
    }

    /**
     * 인증 실패 시 처리하는 {@link AuthenticationEntryPointConfig} 구성
     * <div>
     * <b>case 1. Local 환경인 경우:</b>
     * <p>
     * delegatingAuthenticationEntryPoint = loginUrlAuthenticationEntryPoint(text/html) problemDetailEntryPoint(application/json, default)
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

/*
 * <pre>
 * Google Login
 * https://accounts.google.com/signin/oauth/consent
 * ?authuser=0
 * &part=AJi8hANuqOBYmg5-rk-wOgJMu7DJ1K54Zc6LZMJN1sOn1NuJmApYsjy2j0lFTuZNEEpWpWDi6_rKWC1TRdS15jDSI7TjANbZQbgdIagKNF7acjR7o3yw95CYntMSLLzxkvAw7oSjQ_HvkCm3YE-Fw6DTs9e29lgPUlTpkcS6k2sACjOLJx_eNy5BTU2Y5AM5dtlSa3-gvYn-mBLf87f9MBIe2AUswDiexyPuwCyltK7_C2cjVOsThaiSwWtJM5K6vl8E1HkqQJJLlmt7ZCX64ZVoGL764awpj5n-IlFSkf_R3mhlVZ2vK9xos1MK-HsBNpKqo_Wi82ddl4PX_oOswXZUU7YnNPkxL-2hvS_85J_lbF4OWpN_QPfV6bKpQoKpBQ8DZw_edSmPkPMeaBsrfNi4nuLWh9RRvkD2LTPyO4TV2TgCSYnXcAtt2OITud9QTq5vxUogRUVaxkpVA0Xz6pvgUtl7HUJbTQ
 * &as=S-1559799320%3A1691727533378059
 * &client_id=69090294389-2pnugpnhjdm5odpk89kdjc318860pb9b.apps.googleusercontent.com
 * &pli=1
 * &rapt=AEjHL4N5TuPZuF0PcWieOqL12t2q34WHmjBiFsQ9_2RxqUDd9JNAZhEA7m2GbKn5RY7KDsvz0utXuFFQ4G667RcPLSO1ntdmug
 *
 * After Login -> redirect KM
 * http://localhost:8080/login/oauth2/code/google
 * ?state=j73vUFaJtZJ0psQeiPgjEnR2f59Qm2O523eC2PSAFaA%3D
 * &code=4%2F0Adeu5BXsefdEaUGIXRUh3qy_l8ABlduitekYxiBXwOvZOrozG4kGuxrc8mooKC_vvZgqEg
 * &scope=email+profile+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+openid
 * &authuser=0
 * &hd=groundx.xyz
 * &prompt=consent
 * </pre>
 */
