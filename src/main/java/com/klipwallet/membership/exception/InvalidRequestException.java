package com.klipwallet.membership.exception;

@SuppressWarnings("serial")
public class InvalidRequestException extends BaseCodeException {

    public InvalidRequestException() {
        super(ErrorCode.INVALID_REQUEST);
    }
}
