package com.klipwallet.membership.entity;

import java.util.Collection;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2Token;

import static com.klipwallet.membership.config.SecurityConfig.*;


/**
 * Klip Membership 인증된 사용자 인터페이스
 * <p>
 * 서비스 접속 시 Context 용으로 사용된다.
 * </p>
 *
 * @see org.springframework.security.core.annotation.AuthenticationPrincipal
 * @see com.klipwallet.membership.config.security.KlipMembershipOAuth2User
 */
public interface AuthenticatedUser extends AuthenticatedPrincipal {
    @Nullable
    MemberId getMemberId();

    /**
     * oauth2: social-id
     * <p>
     * 일부 provider에서는 이메일이 반환될 수도 있다. (구글은 번호가 반환됨)
     * </p>
     */
    @Nonnull
    @Override
    String getName();

    /**
     * 이메일 반환
     */
    String getEmail();

    Collection<? extends GrantedAuthority> getAuthorities();

    @JsonIgnore
    default boolean isKakao() {
        return getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(ROLE_KLIP_KAKAO));
    }

    @JsonIgnore
    default boolean isGoogle() {
        return getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(OAUTH2_USER));
    }

    @JsonIgnore
    default boolean isAdmin() {
        return getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(ROLE_ADMIN));
    }

    @JsonIgnore
    default boolean isPartner() {
        return getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(ROLE_PARTNER));
    }

    /**
     * 카카오 OAuth로 인증했을 때 카카오 이용자 휴대폰 번호.
     * <p>카카오 이용자가 아닌 경우 {@code null} 반환</p>
     */
    @Nullable
    String getKakaoPhoneNumber();

    @SuppressWarnings("unused")
    @Nullable
    OAuth2Token getKakaoAccessToken();
}
