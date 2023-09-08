package com.klipwallet.membership.dto;

import jakarta.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import com.klipwallet.membership.entity.Statusable;

/**
 * Kakao 인증을 통한 One-time Action
 */
@Getter
public enum OneTimeAction implements Statusable {
    /**
     * 없음
     */
    NONE(0),
    /**
     * 운영진 초대
     */
    INVITE_OPERATOR(1),
    /**
     * 오픈채팅 참여
     */
    JOIN_OPENCHATTING(2);

    private final byte code;

    OneTimeAction(int code) {
        this.code = Statusable.requireVerifiedCode(code);
    }

    @JsonCreator
    @Nonnull
    public static OneTimeAction fromDisplay(String display) {
        OneTimeAction result = Statusable.fromDisplay(OneTimeAction.class, display);
        if (result == null) {
            return NONE;
        }
        return result;
    }

    @JsonValue
    @Override
    public String toDisplay() {
        return Statusable.super.toDisplay();
    }
}
