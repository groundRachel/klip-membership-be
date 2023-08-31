package com.klipwallet.membership.dto.partnerapplication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.lang.Nullable;

import com.klipwallet.membership.entity.Statusable;

@Getter
@Schema(name = "PartnerApplication.SignUpStatus", description = "파트너 가입 상태", example = "approved")
public enum SignUpStatus implements Statusable {
    /**
     * 가입 전 상태 (가입 거절 상태 포함)
     */
    NON_MEMBER(0),
    /**
     * 가입 요청한 상태
     */
    PENDING(1),
    /**
     * 가입 완료 상태
     */
    SIGNED_UP(2);

    private final byte code;

    SignUpStatus(int code) {
        this.code = Statusable.requireVerifiedCode(code);
    }

    @JsonCreator
    @Nullable
    public static SignUpStatus fromDisplay(String display) {
        return Statusable.fromDisplay(SignUpStatus.class, display);
    }

    @JsonValue
    @Override
    public String toDisplay() {
        return Statusable.super.toDisplay();
    }
}
