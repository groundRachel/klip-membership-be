package com.klipwallet.membership.entity;

import java.util.Collection;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;

import com.klipwallet.membership.entity.kakao.KakaoId;


/**
 * Klip Membership 인증된 사용자 ValueObject
 * <p>
 * 서비스 접속 시 Context 용으로 사용된다.
 * </p>
 *
 * @see org.springframework.security.core.userdetails.User
 * @see org.springframework.security.core.annotation.AuthenticationPrincipal
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class AuthenticatedUser extends org.springframework.security.core.userdetails.User {
    UserId id;
    KakaoId kakaoId;

    /**
     * 멤버 생성 시 사용.
     */
    public AuthenticatedUser(@NonNull UserId id, @Nullable KakaoId kakaoId, String username, String password,
                             boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
                             Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
        this.kakaoId = kakaoId;
    }

    /**
     * 카카오 계정 연동이 되었는가?
     */
    public boolean isLinkedToKakao() {
        return kakaoId != null && kakaoId.getId() != null;
    }
}
