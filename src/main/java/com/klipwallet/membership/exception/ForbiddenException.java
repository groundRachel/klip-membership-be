package com.klipwallet.membership.exception;

import com.klipwallet.membership.entity.AuthenticatedUser;

/**
 * <b>기본 권한 부족 예외</b>
 * <p>
 * 일반적으로 {@literal http}로 응답시 {@code Status 403}을 반환한다.
 * </p>
 */
public class ForbiddenException extends BaseException {
    /**
     * 권한이 부족한 멤버 객체.
     */
    private final AuthenticatedUser member;

    public ForbiddenException(AuthenticatedUser member) {
        super();
        this.member = member;
    }

    public AuthenticatedUser getMember() {
        return member;
    }
}
