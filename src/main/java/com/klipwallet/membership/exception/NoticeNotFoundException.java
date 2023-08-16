package com.klipwallet.membership.exception;

public class NoticeNotFoundException extends NotFoundException {
    public static final String CODE = "error.notice.not-found.1";

    public NoticeNotFoundException(Integer noticeId) {
        super(CODE, noticeId);
    }
}
