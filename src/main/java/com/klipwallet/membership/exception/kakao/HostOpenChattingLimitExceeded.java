package com.klipwallet.membership.exception.kakao;

import jakarta.validation.constraints.NotNull;

import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.InvalidRequestException;

public class HostOpenChattingLimitExceeded extends InvalidRequestException {
    public HostOpenChattingLimitExceeded(@NotNull Long hostId, @NotNull Long count) {
        super(ErrorCode.HOST_OPEN_CHATTING_LIMIT_EXCEEDED, hostId, count);
    }
}
