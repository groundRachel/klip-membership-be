package com.klipwallet.membership.exception;

import java.util.stream.Stream;

import jakarta.annotation.Nullable;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

/**
 * 오류 코드 모음
 */
@Getter
public enum ErrorCode {
    INVALID_REQUEST(400_000),

    UNAUTHENTICATED(401_000),
    FORBIDDEN(403_000),
    NOT_FOUND(404_000),
    CONFLICT(409_000),
    INTERNAL_SERVER_ERROR(500_000),
    UNKNOWN(500_999),

    // NOTICE_*
    NOTICE_NOT_FOUND(404_001),


    // Partner_*
    PARTNER_APPLICATION_NOT_FOUND(404_002),
    PARTNER_NOT_FOUND(404_003),
    PARTNER_APPLICATION_ALREADY_PROCESSED(409_001);

    final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    @Nullable
    public static ErrorCode fromStatusCode(HttpStatusCode statusCode) {
        int candidateCode = statusCode.value() * 1000;
        return Stream.of(values())
                     .filter(c -> c.code == candidateCode)
                     .findFirst()
                     .orElse(null);
    }

    /**
     * For {@link org.springframework.context.MessageSource}
     */
    public String toMessageCode() {
        return "problemDetail.code.%s".formatted(code);
    }
}


