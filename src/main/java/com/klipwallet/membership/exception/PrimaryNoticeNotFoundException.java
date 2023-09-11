package com.klipwallet.membership.exception;

/**
 * 고정 공지를 찾을 수 없습니다.
 */
@SuppressWarnings("serial")
public class PrimaryNoticeNotFoundException extends NoticeNotFoundException {
    public PrimaryNoticeNotFoundException() {
        super(ErrorCode.PRIMARY_NOTICE_NOT_FOUND);
    }
}
