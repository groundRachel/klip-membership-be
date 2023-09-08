package com.klipwallet.membership.adaptor.kakao.biztalk.dto;

import jakarta.annotation.Nullable;

import lombok.NonNull;

/**
 * BGMS 사용자 토큰 요청 DTO
 *
 * @param bsid   비즈톡에서 발급한 ID
 * @param passwd 비즈톡에서 발급한 PW
 * @param expire 토큰 유효기간.
 *               <p>
 *               토큰 유효기간. 분 단위로 설정한다. 최소값은 60(1시간), 최대 값은 1440(24시간)이다. 미 입력 시 24시간을 기본으로 한다.
 *               </p>
 */
public record BgmsTokenReq(@NonNull String bsid, @NonNull String passwd, @Nullable Integer expire) {
    /**
     * 유효기간이 최대인 24시간이 토큰 발급
     */
    public static BgmsTokenReq expiredMax(String bsid, String passwd) {
        return new BgmsTokenReq(bsid, passwd, null);
    }
}
