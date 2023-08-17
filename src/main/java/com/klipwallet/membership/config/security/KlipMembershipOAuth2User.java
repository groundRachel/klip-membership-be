package com.klipwallet.membership.config.security;

import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;

import jakarta.annotation.Nonnull;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.MemberId;

@RequiredArgsConstructor
@ToString
public class KlipMembershipOAuth2User implements AuthenticatedUser {
    private final MemberId memberId;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String name;
    private final String email;

    static KlipMembershipOAuth2User notMemberOnGoogle(OAuth2User googleUser) {
        return new KlipMembershipOAuth2User(null, googleUser.getAttributes(), googleUser.getAuthorities(), googleUser.getName(),
                                            getGoogleEmail(googleUser.getAttributes()));
    }

    // FIXME @Jordan Add Partner arg. Processing ADMIN, SUPER_ADMIN
    static KlipMembershipOAuth2User memberOnGoogle(OAuth2User googleUser) {
        return new KlipMembershipOAuth2User(new MemberId(2), googleUser.getAttributes(),
                                            AuthorityUtils.createAuthorityList("ROLE_PARTNER"),
                                            googleUser.getName(),
                                            getGoogleEmail(googleUser.getAttributes()));
    }

    private static String getGoogleEmail(Map<String, Object> attributes) {
        return (String) attributes.getOrDefault("email", null);
    }

    @Nonnull
    @Override
    public String getName() {
        return this.name;
    }

    @Nullable
    @Override
    public String getEmail() {
        return this.email;
    }

    @Nullable
    @Override
    public MemberId getMemberId() {
        return this.memberId;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }
}