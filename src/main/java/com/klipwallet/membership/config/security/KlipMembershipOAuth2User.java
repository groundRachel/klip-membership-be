package com.klipwallet.membership.config.security;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import jakarta.annotation.Nonnull;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.klipwallet.membership.entity.Admin;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Partner;

import static com.klipwallet.membership.config.SecurityConfig.*;

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
                                            getGoogleEmail(googleUser));
    }

    private static String getGoogleEmail(Map<String, Object> attributes) {
        return (String) attributes.getOrDefault("email", null);
    }

    public static String getGoogleEmail(OAuth2User oAuth2User) {
        return getGoogleEmail(oAuth2User.getAttributes());
    }

    public static KlipMembershipOAuth2User partnerOnGoogle(Partner partner, OAuth2User googleUser) {
        return new KlipMembershipOAuth2User(partner.getMemberId(), googleUser.getAttributes(),
                                            AuthorityUtils.createAuthorityList(ROLE_PARTNER),
                                            googleUser.getName(),
                                            getGoogleEmail(googleUser));
    }

    public static KlipMembershipOAuth2User adminOnGoogle(Admin admin, OAuth2User googleUser) {
        List<GrantedAuthority> authorities = getAuthorities(admin);
        return new KlipMembershipOAuth2User(admin.getMemberId(), googleUser.getAttributes(),

                                            authorities,
                                            googleUser.getName(),
                                            getGoogleEmail(googleUser));
    }

    @NonNull
    private static List<GrantedAuthority> getAuthorities(Admin admin) {
        if (admin.isSuper()) {  // 슈퍼 어드민
            return AuthorityUtils.createAuthorityList(ROLE_SUPER_ADMIN, ROLE_ADMIN);
        }
        // 일반 어드민
        return AuthorityUtils.createAuthorityList(ROLE_ADMIN);
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
