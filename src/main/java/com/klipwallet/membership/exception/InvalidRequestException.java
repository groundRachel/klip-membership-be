package com.klipwallet.membership.exception;

public class InvalidRequestException extends BaseCodeException {

    public InvalidRequestException() {
        super(ErrorCode.INVALID_REQUEST);
    }
}
