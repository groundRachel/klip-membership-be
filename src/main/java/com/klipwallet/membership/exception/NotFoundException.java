package com.klipwallet.membership.exception;

/**
 * <b>리소스 Not Found 예외. {@code error.not-found}</b>
 * <p>
 * 일반적으로 {@literal http}로 응답시 {@code Status 404}을 반환한다.
 * </p>
 */
@SuppressWarnings("unused")
public class NotFoundException extends BaseCodeException {
    public NotFoundException() {
        super(ErrorCode.NOT_FOUND);
    }

    public NotFoundException(ErrorCode code) {
        super(code);
    }

    public NotFoundException(ErrorCode code, Object... args) {
        super(code, args);
    }
}
