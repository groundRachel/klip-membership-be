package com.klipwallet.membership.exception;

@SuppressWarnings({"serial", "unused"})
public class InvalidRequestException extends BaseCodeException {

    public InvalidRequestException() {
        super(ErrorCode.INVALID_REQUEST);
    }

    public InvalidRequestException(String message) {
        super(ErrorCode.INVALID_REQUEST, message);
    }

    public InvalidRequestException(ErrorCode code) {
        super(code);
    }

    public InvalidRequestException(ErrorCode code, Throwable cause) {
        super(code, cause);
    }

    public InvalidRequestException(ErrorCode code, Object... args) {
        super(code, args);
    }
}
