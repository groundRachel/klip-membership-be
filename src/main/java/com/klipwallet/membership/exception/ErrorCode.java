package com.klipwallet.membership.exception;

import java.util.stream.Stream;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

/**
 * 오류 코드
 * <p>
 * 오류 코드 Enum 상수 추가 시 오류 코드에 해당하는 메시지 정보를 {@code /src/main/resources/message/error.xml} 에 같이 추가해야함.
 * 예를 들어서 {@code PARTNER_NOT_FOUND(404_002),} Enum 상수를 추가한다면, {@code error.xml}에 아래 같이 추가해아함.
 * <pre>
 * <entry key="problemDetail.code.404002">파트너를 찾을 수 없습니다. ID: {0}</entry>
 * </pre>
 * key 적용 시 {@code problemDetail.code.} prefix를 붙여야함.
 * </p>
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
    PRIMARY_NOTICE_NOT_FOUND(404_004),

    // Partner_APPLICATION_*
    PARTNER_APPLICATION_NOT_FOUND(404_002),
    PARTNER_APPLICATION_ALREADY_PROCESSED(409_001),

    // Partner_*,
    PARTNER_NOT_FOUND(404_003);

    final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public static ErrorCode fromStatusCode(HttpStatusCode statusCode) {
        int candidateCode = statusCode.value() * 1000;
        return Stream.of(values())
                     .filter(c -> c.code == candidateCode)
                     .findFirst()
                     .orElse(ErrorCode.UNKNOWN);
    }

    /**
     * For {@link org.springframework.context.MessageSource}
     */
    public String toMessageCode() {
        return "problemDetail.code.%s".formatted(code);
    }
}


