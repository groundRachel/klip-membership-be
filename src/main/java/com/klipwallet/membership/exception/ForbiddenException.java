package com.klipwallet.membership.exception;

import lombok.Getter;

import com.klipwallet.membership.entity.AuthenticatedUser;

/**
 * <b>기본 권한 부족 예외. {@code error.forbidden}</b>
 * <p>
 * 일반적으로 {@literal http}로 응답시 {@code Status 403}을 반환한다.
 * </p>
 */
@Getter
public class ForbiddenException extends BaseCodeException {
    /**
     * 권한이 부족한 User 객체.
     */
    private AuthenticatedUser user;

    public ForbiddenException(ErrorCode code) {
        super(code);
    }

    public ForbiddenException(AuthenticatedUser user) {
        super(ErrorCode.FORBIDDEN);
        this.user = user;
    }
}
