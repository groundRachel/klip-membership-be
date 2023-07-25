package com.klipwallet.membership.exception;

import lombok.Getter;

import com.klipwallet.membership.entity.AuthenticatedUser;

/**
 * <b>기본 권한 부족 예외. {@code error.forbidden}</b>
 * <p>
 * 일반적으로 {@literal http}로 응답시 {@code Status 403}을 반환한다.
 * </p>
 */
public class ForbiddenException extends BaseMessageException {
    /**
     * 권한이 부족한 User 객체.
     */
    @Getter
    private final AuthenticatedUser user;

    public ForbiddenException(AuthenticatedUser user) {
        super("error.forbidden");
        this.user = user;
    }
}
