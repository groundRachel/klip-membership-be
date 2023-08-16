package com.klipwallet.membership.exception;

import lombok.Getter;

/**
 * 오류 코드 모음
 */
@Getter
public enum ErrorCode {
    INVALID_REQUEST(400_000),
    UNAUTHENTICATED(401_000),
    FORBIDDEN(403_000),
    NOT_FOUND(404_000),
    INTERNAL_SERVER_ERROR(500_000),
    UNKNOWN(500_999),

    NOTICE_NOT_FOUND(404_001);

    final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    /**
     * For {@link org.springframework.context.MessageSource}
     */
    public String toMessageCode() {
        return "error.%s".formatted(code);
    }
}


