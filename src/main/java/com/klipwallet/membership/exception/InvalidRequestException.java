package com.klipwallet.membership.exception;

public class InvalidRequestException extends BaseMessageException {

    public InvalidRequestException() {
        super("error.invalid");
    }

    public InvalidRequestException(String code) {
        super(code);
    }

    public InvalidRequestException(String code, Object... args) {
        super(code, args);
    }
}
