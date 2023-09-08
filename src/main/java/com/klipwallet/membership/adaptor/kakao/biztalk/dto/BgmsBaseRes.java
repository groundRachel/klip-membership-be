package com.klipwallet.membership.adaptor.kakao.biztalk.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import com.klipwallet.membership.dto.InternalApiError;

@Getter
@RequiredArgsConstructor
@ToString
public class BgmsBaseRes implements InternalApiError {
    static final String CODE_SUCCESS = "1000";
    /**
     * 비즈톡 G/W 접수결과 코드(결과 코드는 결과 코드표에서 확인)
     */
    private final String responseCode;
    /**
     * 실패 메세지(실패일 경우에만 표시)
     */
    private final String msg;

    @JsonIgnore
    public boolean isSuccessful() {
        return responseCode.equals(CODE_SUCCESS);
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
