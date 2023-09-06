package com.klipwallet.membership.exception;

@SuppressWarnings({"serial", "unused"})
public class ExpiredException extends BaseCodeException {
    public ExpiredException() {
        super(ErrorCode.EXPIRED);
    }

    public ExpiredException(String message) {
        super(ErrorCode.INVALID_REQUEST, message);
    }

    public ExpiredException(ErrorCode code) {
        super(code);
    }

    public ExpiredException(ErrorCode code, Object... args) {
        super(code, args);
    }
}
