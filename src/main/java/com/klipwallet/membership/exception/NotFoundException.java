package com.klipwallet.membership.exception;

import com.klipwallet.membership.entity.AuthenticatedUser;

/**
 * <b>리소스 Not Found 예외. {@code error.not-found}</b>
 * <p>
 * 일반적으로 {@literal http}로 응답시 {@code Status 404}을 반환한다.
 * </p>
 */
public class NotFoundException extends BaseMessageException {
    public NotFoundException(String message) {
        super(message);
    }
}
