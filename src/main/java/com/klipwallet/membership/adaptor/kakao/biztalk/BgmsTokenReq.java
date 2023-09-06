package com.klipwallet.membership.adaptor.kakao.biztalk;

import jakarta.annotation.Nullable;

import lombok.NonNull;
import lombok.Value;

/**
 * BGMS 사용자 토큰 요청 DTO
 */
@SuppressWarnings("ClassCanBeRecord")
@Value
public class BgmsTokenReq {
    /**
     * 비즈톡에서 발급한 ID
     */
    @NonNull
    String bsid;
    /**
     * 비즈톡에서 발급한 PW
     */
    @NonNull
    String passwd;
    /**
     * 토큰 유효기간.
     * <p>
     * 토큰 유효기간. 분 단위로 설정한다. 최소값은 60(1시간), 최대 값은 1440(24시간)이다. 미 입력 시 24시간을 기본으로 한다.
     * </p>
     */
    @Nullable
    Integer expire;

    /**
     * 유효기간이 최대인 24시간이 토큰 발급
     */
    public static BgmsTokenReq expiredMax(String bsid, String passwd) {
        return new BgmsTokenReq(bsid, passwd, null);
    }
}
