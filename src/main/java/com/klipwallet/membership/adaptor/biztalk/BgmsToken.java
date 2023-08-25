package com.klipwallet.membership.adaptor.biztalk;

import java.time.LocalDateTime;

import io.micrometer.common.util.StringUtils;
import lombok.Value;

@Value
public class BgmsToken {
    /**
     * 사용자 토큰
     */
    String token;
    /**
     * 만료일시(로컬에 저장됨)
     */
    LocalDateTime expiredAt;


    public boolean exists() {
        return StringUtils.isNotBlank(this.token);
    }

    /**
     * 토큰이 만료되었는가?
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    /**
     * 토큰이 곧 만료되는가?(1시간 전)
     * <p>
     * 미리 토큰을 준비할려고 함
     * </p>
     */
    public boolean isExpiredSoon() {
        return LocalDateTime.now().isAfter(expiredAt.minusHours(1L));
    }
}
