package com.klipwallet.membership.exception.notifier;

import com.klipwallet.membership.exception.BaseCodeException;
import com.klipwallet.membership.exception.ErrorCode;

public class EmailNotifierException extends BaseCodeException {

    public EmailNotifierException(Throwable cause) {
        super(ErrorCode.Email_SEND, cause);
    }

    public EmailNotifierException(String msg) {
        super(ErrorCode.Email_SEND, msg);
    }

    public EmailNotifierException() {
        super(ErrorCode.Email_SEND, "");
    }
}
