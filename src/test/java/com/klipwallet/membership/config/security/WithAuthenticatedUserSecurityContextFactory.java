package com.klipwallet.membership.config.security;

import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.klipwallet.membership.entity.MemberId;

public class WithAuthenticatedUserSecurityContextFactory implements WithSecurityContextFactory<WithAuthenticatedUser> {

    public SecurityContext createSecurityContext(WithAuthenticatedUser user) {
        SecurityContext context = SecurityContextHolder.getContextHolderStrategy().createEmptyContext();
        KlipMembershipOAuth2User principal = new KlipMembershipOAuth2User(
                new MemberId(user.memberId()), Collections.emptyMap(), AuthorityUtils.createAuthorityList(user.authorities()), user.name(),
                user.email());
        Authentication auth = new OAuth2AuthenticationToken(principal, principal.getAuthorities(), "google");
        context.setAuthentication(auth);
        return context;
    }
}
