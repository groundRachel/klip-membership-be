package com.klipwallet.membership.exception;

@SuppressWarnings("serial")
public class PrimaryNoticeNotFoundException extends NotFoundException {
    public PrimaryNoticeNotFoundException() {
        super(ErrorCode.PRIMARY_NOTICE_NOT_FOUND);
    }
}
