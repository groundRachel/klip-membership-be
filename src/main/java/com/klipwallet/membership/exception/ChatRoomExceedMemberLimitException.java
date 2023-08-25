package com.klipwallet.membership.exception;

public class ChatRoomExceedMemberLimitException extends InvalidRequestException {
    // TODO: Ian, refactor after https://github.com/ground-x/klip-membership-be/pull/7 pr merged
    public static final String CODE = "error.openchat.invalid-request.1";

    public ChatRoomExceedMemberLimitException(Long size) {
        //        super(CODE, size);
    }
}
