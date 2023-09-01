package com.klipwallet.membership.adaptor.klipdrops.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

import com.klipwallet.membership.entity.Statusable;

@Schema(name = "KlipDrops.PartnerStatus", description = "드롭 상태", example = "not_registered")
public enum KlipDropsPartnerStatus implements Statusable {
    NOT_REGISTERED(0) {
        @Override
        public String toDisplay() {
            return "not_registered";
        }
    },
    PENDING(1),
    ACTIVE(2),
    INACTIVE(3);

    private final byte code;

    KlipDropsPartnerStatus(int code) {
        this.code = Statusable.requireVerifiedCode(code);
    }

    @JsonCreator
    @Nullable
    public static KlipDropsPartnerStatus fromDisplay(String display) {
        return Statusable.fromDisplay(KlipDropsPartnerStatus.class, display);
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