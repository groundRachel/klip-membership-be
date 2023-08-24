package com.klipwallet.membership.dto;

/**
 * 내부 API 호출 오류 모델
 * <p>
 * KAKAO, Biztalk 등 내부 의존하는 API 호출 시 오류 모델
 * </p>
 */
public interface InternalApiError {
    /**
     * API 오류 코드
     */
    String getCode();

    /**
     * API 오류 메시지
     */
    String getMessage();
}
