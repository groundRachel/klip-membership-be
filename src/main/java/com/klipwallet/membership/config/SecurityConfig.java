package com.klipwallet.membership.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.klipwallet.membership.config.security.KlipMembershipOAuth2UserService;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class SecurityConfig {
    /**
     * {@code local} 환경에서 {@code /h2-console} 접근을 위한 보안 약화 설정
     */
    @SuppressWarnings("Convert2MethodRef")
    @Order(-1)
    @Profile("local")
    @Bean
    public SecurityFilterChain localFilterChain(HttpSecurity http) throws Exception {
        // /h2-console
        http.securityMatcher(antMatcher("/h2-console/**"))
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

    @SuppressWarnings("Convert2MethodRef")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(c -> c.disable())
            .authorizeHttpRequests(
                    a -> a.requestMatchers(antMatcher("/tool/**")).hasRole("PARTNER")
                          .requestMatchers(antMatcher("/tool/faq/*"), antMatcher("/tool/members/me")).permitAll()
                          .requestMatchers(antMatcher("/tool/partners/apply")).hasAuthority("OAUTH2_USER")
                          .requestMatchers(antMatcher("/swagger-ui/**"), antMatcher("/v3/api-docs/**")).permitAll()
                          .requestMatchers(antMatcher("/error")).permitAll()
                          .requestMatchers(antMatcher("/admin/**")).hasRole("ADMIN")
                          .requestMatchers(antMatcher("/oauth")).hasAuthority("OAUTH2_USER")
                          .requestMatchers(antMatcher("/user"),
                                           antMatcher("/usera"),
                                           antMatcher("/usero")).permitAll()
                          .anyRequest().authenticated())
            .oauth2Login(withDefaults())
            .anonymous(a -> a.disable())
            .httpBasic(h -> h.disable())
            .formLogin(f -> f.disable())
            .rememberMe(r -> r.disable());

        return http.build();
    }

    /**
     * Implementation of OAuth2UserService
     */
    @Bean
    KlipMembershipOAuth2UserService oauth2UserService() {
        return new KlipMembershipOAuth2UserService();
    }
}

/**
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
