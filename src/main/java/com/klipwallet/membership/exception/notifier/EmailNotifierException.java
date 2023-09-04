package com.klipwallet.membership.exception.notifier;

import com.klipwallet.membership.exception.BaseCodeException;
import com.klipwallet.membership.exception.ErrorCode;

public class EmailNotifierException extends BaseCodeException {

    public EmailNotifierException(Throwable cause) {
        super(ErrorCode.EMAIL_SEND_ERROR, cause);
    }

    public EmailNotifierException(String msg) {
        super(ErrorCode.EMAIL_SEND_ERROR, msg);
    }

    public EmailNotifierException() {
        super(ErrorCode.EMAIL_SEND_ERROR, "");
    }
}
