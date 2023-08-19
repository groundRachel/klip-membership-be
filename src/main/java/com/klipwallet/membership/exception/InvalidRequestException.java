package com.klipwallet.membership.exception;

@SuppressWarnings("serial")
public class InvalidRequestException extends BaseCodeException {

    public InvalidRequestException() {
        super(ErrorCode.INVALID_REQUEST);
    }

    public InvalidRequestException(ErrorCode code) {
        super(code);
    }

    public InvalidRequestException(ErrorCode code, Object... args) {
        super(code, args);
    }
}
