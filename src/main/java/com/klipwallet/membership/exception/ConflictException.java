package com.klipwallet.membership.exception;

public class ConflictException extends BaseCodeException {

    public ConflictException() {
        super(ErrorCode.CONFLICT);
    }

    public ConflictException(ErrorCode code) {
        super(code);
    }

    public ConflictException(ErrorCode code, Object... args) {
        super(code, args);
    }
}
