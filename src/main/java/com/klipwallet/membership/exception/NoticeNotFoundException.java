package com.klipwallet.membership.exception;

@SuppressWarnings("serial")
public class NoticeNotFoundException extends NotFoundException {
    public NoticeNotFoundException(Integer noticeId) {
        super(ErrorCode.NOTICE_NOT_FOUND, noticeId);
    }
}
