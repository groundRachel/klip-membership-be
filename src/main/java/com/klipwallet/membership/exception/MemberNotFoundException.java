package com.klipwallet.membership.exception;

/**
 * 이용자가 존재하지 않습니다.
 */
@SuppressWarnings("serial")
public class MemberNotFoundException extends NotFoundException {
    public MemberNotFoundException() {
        super(ErrorCode.MEMBER_NOT_FOUND);
    }

}
