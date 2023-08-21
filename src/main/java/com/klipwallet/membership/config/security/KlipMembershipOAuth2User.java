package com.klipwallet.membership.config.security;

import java.io.Serial;
import java.io.Serializable;
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

@SuppressWarnings("ClassCanBeRecord")
@RequiredArgsConstructor
@ToString
public class KlipMembershipOAuth2User implements AuthenticatedUser, Serializable {
    @Serial
    private static final long serialVersionUID = 9102776982135701748L;

    private final MemberId memberId;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String name;
    private final String email;

    @SuppressWarnings("unused")
    static KlipMembershipOAuth2User notMemberOnGoogle(OAuth2User googleUser) {
        return new KlipMembershipOAuth2User(null, googleUser.getAttributes(), googleUser.getAuthorities(), googleUser.getName(),
                                            getGoogleEmail(googleUser.getAttributes()));
    }

    // FIXME @Jordan Add Partner arg. Processing ADMIN, SUPER_ADMIN
    static KlipMembershipOAuth2User memberOnGoogle(OAuth2User googleUser) {
        return new KlipMembershipOAuth2User(new MemberId(2), googleUser.getAttributes(),
                                            AuthorityUtils.createAuthorityList("ROLE_PARTNER", "ROLE_ADMIN"),
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
