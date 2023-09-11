package com.klipwallet.membership.exception;

/**
 * 공지사항을 찾을 수 없습니다.
 */
@SuppressWarnings("serial")
public class NoticeNotFoundException extends NotFoundException {
    public NoticeNotFoundException(Integer noticeId) {
        super(ErrorCode.NOTICE_NOT_FOUND, noticeId);
    }

    NoticeNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
