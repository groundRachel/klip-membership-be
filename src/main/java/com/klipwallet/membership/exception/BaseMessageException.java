package com.klipwallet.membership.exception;

import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * Klip Membership 기본 메시지 예외.
 * <p>
 * 애플리케이션 내 도메인을 예외 메시지를 표현하는 모든 예외는 본 {@link com.klipwallet.membership.exception.BaseMessageException}을 상속 받아야한다.
 * {@code /src/resources/message/errors.xml} 에 메시지 코드가 매칭되어야한다.
 * </p>
 */
public abstract class BaseMessageException extends BaseException {
    @Getter
    private final String code;

    public BaseMessageException(String code) {
        this.code = code;
    }

    public BaseMessageException(String code, Throwable cause) {
        super(cause);
        this.code = requireVerifiedCode(code);
    }

    public BaseMessageException(String code, String message) {
        super(message);
        this.code = requireVerifiedCode(code);
    }

    public BaseMessageException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = requireVerifiedCode(code);
    }

    private String requireVerifiedCode(String code) {
        if (!StringUtils.hasText(code)) {
            throw new IllegalArgumentException("code is empty or null: %s".formatted(code));
        }
        if (!code.startsWith("error.")) {
            throw new IllegalArgumentException("code is not starts with 'error.': %s".formatted(code));
        }
        return code;
    }
}
