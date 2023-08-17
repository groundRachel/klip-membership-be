package com.klipwallet.membership.exception;

public class NoticeNotFoundException extends NotFoundException {
    public NoticeNotFoundException(Integer noticeId) {
        super(ErrorCode.NOTICE_NOT_FOUND, noticeId);
    }
}
