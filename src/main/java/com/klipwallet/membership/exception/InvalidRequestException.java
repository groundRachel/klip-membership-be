package com.klipwallet.membership.exception;

import lombok.Getter;

import com.klipwallet.membership.entity.AuthenticatedUser;

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
