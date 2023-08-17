package com.klipwallet.membership.exception;

public class FaqNotFoundException extends NotFoundException {
    public FaqNotFoundException(Integer faqId) {
        super(ErrorCode.FAQ_NOT_FOUND, faqId);
    }
}
