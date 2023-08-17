package com.klipwallet.membership.exception;

import lombok.Getter;
import lombok.ToString;

/**
 * Klip Membership 기본 코드 예외.
 * <p>
 * 애플리케이션 내 도메인을 예외 메시지를 표현하는 모든 예외는 본 {@link BaseCodeException}을 상속 받아야한다.
 * {@code /src/resources/message/errors.xml} 에 메시지 코드가 매칭되어야한다.
 * </p>
 * @see ErrorCode#toMessageCode()
 */
@SuppressWarnings("unused")
@Getter
@ToString
public abstract class BaseCodeException extends BaseException {
    private final ErrorCode errorCode;
    private final Object[] errorArgs;
    public BaseCodeException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorArgs = null;
    }

    public BaseCodeException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.errorArgs = null;
    }

    public BaseCodeException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorArgs = null;
    }

    public BaseCodeException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorArgs = null;
    }

    public BaseCodeException(ErrorCode errorCode, Object[] errorArgs) {
        super(String.valueOf(errorCode));
        this.errorCode = errorCode;
        this.errorArgs = errorArgs;
    }
}
