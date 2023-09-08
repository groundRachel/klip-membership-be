package com.klipwallet.membership.adaptor.kakao.biztalk.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

/**
 * BGMS 사용자 토큰 응답 DTO
 */
@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BgmsTokenRes extends BgmsBaseRes {
    /**
     * 사용자 토큰(성공일 경우에만 표시)
     * <p>
     * JWT
     * </p>
     */
    String token;

    @JsonCreator
    public BgmsTokenRes(@JsonProperty("responseCode") @NonNull String responseCode,
                        @JsonProperty("token") String token,
                        @JsonProperty("msg") String msg) {
        super(responseCode, msg);
        this.token = token;
    }
}
