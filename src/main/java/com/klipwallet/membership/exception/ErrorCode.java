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
    INVALID_REQUEST_BODY(400_001),
    OPERATOR_LIMIT_EXCEEDED(400_002),
    OPERATOR_ALREADY_HOST_EXCEPTION(400_003),
    OPERATOR_NOT_IN_PARTNER(400_004),
    OPERATOR_DUPLICATED(400_005),

    UNAUTHENTICATED(401_000),
    UNAUTHENTICATED_BY_OAUTH2(401_001),

    FORBIDDEN(403_000),
    NOT_FOUND(404_000),
    CONFLICT(409_000),
    INTERNAL_SERVER_ERROR(500_000),
    UNKNOWN(500_999),
    SERVICE_UNAVAILABLE(503_000),
    ASYNC_REQUEST_TIMEOUT(503_001),

    // NOTICE_*
    NOTICE_NOT_FOUND(404_001),
    PRIMARY_NOTICE_NOT_FOUND(404_004),

    // Partner_APPLICATION_*
    PARTNER_APPLICATION_NOT_FOUND(404_002),
    PARTNER_APPLICATION_ALREADY_PROCESSED(409_001),
    PARTNER_APPLICATION_DUPLICATED(409_002),

    // Member
    MEMBER_NOT_FOUND(404_009),

    // Admin
    ADMIN_NOT_FOUND(404_006),
    ADMIN_NOT_FOUND_BY_EMAIL(404_007),

    // Partner_*,
    PARTNER_NOT_FOUND(404_003),

    // Operator_*,
    OPERATOR_NOT_FOUND(404_008),

    // FAQ_*
    FAQ_NOT_FOUND(404_005),

    // Storage & AttachFile
    ATTACH_FILE_UPLOAD_LIMIT_OVER(400_006),
    STORAGE_STORE(500_101),

    // InterApi
    INTERNAL_API_ERROR(500_200);

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


