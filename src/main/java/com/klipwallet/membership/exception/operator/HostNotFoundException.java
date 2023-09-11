package com.klipwallet.membership.exception.operator;

import lombok.NonNull;

import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.NotFoundException;

public class HostNotFoundException extends NotFoundException {
    public HostNotFoundException(@NonNull Long openChattingId) {
        super(ErrorCode.HOST_NOT_FOUND, openChattingId);
    }
}
