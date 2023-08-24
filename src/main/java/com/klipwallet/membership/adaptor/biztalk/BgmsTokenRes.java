package com.klipwallet.membership.adaptor.biztalk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;


@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BgmsTokenRes extends BgmsBaseRes {
    /**
     * 인증 토큰(성공일 경우에만 표시)
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
