package com.klipwallet.membership.exception;

/**
 * Klip Membership 최상위 기본 예외.
 * <p>
 * 애플리케이션 내 도메인을 표현하는 모든 예외는 본 {@link BaseException}을 상속 받아야한다.
 * </p>
 */
public class BaseException extends RuntimeException {
    public BaseException() {
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
