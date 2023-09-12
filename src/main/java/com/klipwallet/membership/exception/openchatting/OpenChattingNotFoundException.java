package com.klipwallet.membership.exception.openchatting;

import lombok.NonNull;

import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.NotFoundException;

public class OpenChattingNotFoundException extends NotFoundException {

    public OpenChattingNotFoundException(@NonNull Long openChattingId) {
        super(ErrorCode.OPEN_CHATTING_NOT_FOUND, openChattingId);
    }
}
