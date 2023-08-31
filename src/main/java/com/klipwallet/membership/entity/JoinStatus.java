package com.klipwallet.membership.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

@Schema(name = "JoinStatus", description = "파트너 가입 상태", example = "NON_MEMBER")
public enum JoinStatus implements Statusable {
    /**
     * 가입 전 상태
     */
    NON_MEMBER(0),
    /**
     * 가입 요청한 상태
     */
    PENDING(1),
    /**
     * 가입 완료 상태
     */
    JOINED(2);

    private final byte code;

    JoinStatus(int code) {
        this.code = Statusable.requireVerifiedCode(code);
    }

    @JsonCreator
    @Nullable
    public static JoinStatus fromDisplay(String display) {
        return Statusable.fromDisplay(JoinStatus.class, display);
    }

    public byte getCode() {
        return this.code;
    }

    @JsonValue
    @Override
    public String toDisplay() {
        return Statusable.super.toDisplay();
    }
}
