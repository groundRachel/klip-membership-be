package com.klipwallet.membership.exception;

public class ChatRoomExceedOperatorLimitException extends InvalidRequestException {
    public ChatRoomExceedOperatorLimitException() {
        super(ErrorCode.OPERATOR_LIMIT_EXCEEDED);
    }
}
