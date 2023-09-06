package com.klipwallet.membership.config.security;

import java.util.Arrays;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.klipwallet.membership.entity.MemberId;

import static com.klipwallet.membership.config.SecurityConfig.ROLE_KLIP_KAKAO;

public class WithAuthenticatedUserSecurityContextFactory implements WithSecurityContextFactory<WithAuthenticatedUser> {

    public SecurityContext createSecurityContext(WithAuthenticatedUser user) {
        SecurityContext context = SecurityContextHolder.getContextHolderStrategy().createEmptyContext();
        Authentication authentication = createAuthentication(user);
        context.setAuthentication(authentication);
        return context;
    }

    private Authentication createAuthentication(WithAuthenticatedUser user) {
        if (isKakao(user)) {
            Map<String, Object> attributes = Map.of("kakao_account", Map.of("phone_number", user.kakaoPhoneNumber()));
            KlipMembershipOAuth2User principal = new KlipMembershipOAuth2User(
                    new MemberId(user.memberId()), attributes, AuthorityUtils.createAuthorityList(user.authorities()), user.name(), user.email());
            return new OAuth2AuthenticationToken(principal, principal.getAuthorities(), "kakao");
        }
        KlipMembershipOAuth2User principal = new KlipMembershipOAuth2User(
                new MemberId(user.memberId()), AuthorityUtils.createAuthorityList(user.authorities()), user.name(), user.email());
        return new OAuth2AuthenticationToken(principal, principal.getAuthorities(), "google");
    }

    private boolean isKakao(WithAuthenticatedUser user) {
        return Arrays.asList(user.authorities()).contains(ROLE_KLIP_KAKAO);
    }
}
