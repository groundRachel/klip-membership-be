package com.klipwallet.membership.exception.kakao;

import lombok.NonNull;

import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.exception.ForbiddenException;

/**
 * 카카오 연동이 안된 경우 발생하는 예외
 * <p>
 * 특정 멤버가 카카오 연동이 안된 경우 권한 제한이 있기 때문에 사용된다.
 * 본 예외가 발생하는 경우에는 아래와 같이 처리한다.
 * <pre>
 *     1. 카카오 계정 연동 유도
 *     2. 403과 같은 권한 없음
 * </pre>
 * </p>
 *
 * @see com.klipwallet.membership.exception.ForbiddenException
 */
public class KakaoAccountLinkRequiredException extends ForbiddenException {
    public KakaoAccountLinkRequiredException(@NonNull AuthenticatedUser member) {
        super(member);
    }
}
