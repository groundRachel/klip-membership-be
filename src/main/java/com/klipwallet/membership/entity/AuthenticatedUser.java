package com.klipwallet.membership.entity;

import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import org.springframework.lang.Nullable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
    Integer memberId;
    KakaoId kakaoId;

    /**
     * 기본 생성자.
     */
    public AuthenticatedUser(@NonNull Integer memberId, @Nullable KakaoId kakaoId, String username, @NonNull String role) {
        super(username, "[PASSWORD]", true, true, true, true,
              Set.of(new SimpleGrantedAuthority("ROLE_" + role)));
        this.memberId = memberId;
        this.kakaoId = kakaoId;
    }

    /**
     * 카카오 계정 연동이 되었는가?
     */
    public boolean isLinkedToKakao() {
        return kakaoId != null && kakaoId.getId() != null;
    }
}
