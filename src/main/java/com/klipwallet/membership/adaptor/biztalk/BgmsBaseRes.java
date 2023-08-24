package com.klipwallet.membership.adaptor.biztalk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import com.klipwallet.membership.dto.InternalApiError;

@Getter
@RequiredArgsConstructor
@ToString
abstract class BgmsBaseRes implements InternalApiError {
    static final String CODE_SUCCESS = "1000";
    /**
     * 비즈톡 G/W 접수결과 코드
     */
    private final String responseCode;
    /**
     * 실패 메세지(실패일 경우에만 표시)
     */
    private final String msg;

    @JsonIgnore
    boolean isSuccessful() {
        return responseCode.equals("1000");
    }

    @Override
    public String getCode() {
        return this.getResponseCode();
    }

    @Override
    public String getMessage() {
        return this.getMsg();
    }
}
