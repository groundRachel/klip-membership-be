package com.klipwallet.membership.adaptor.klipdrops.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

import com.klipwallet.membership.entity.Statusable;

@Schema(name = "KlipDrops.DropStatus", description = "드롭 상태", example = "onSale")
public enum DropStatus implements Statusable {
    onSale(1),
    scheduledForSale(2),
    endOfSale(3),
    stopSale(4),
    inactiveDrop(5);

    private final byte code;

    DropStatus(int code) {
        this.code = Statusable.requireVerifiedCode(code);
    }

    @JsonCreator
    @Nullable
    public static DropStatus fromDisplay(String display) {
        return Statusable.fromDisplay(DropStatus.class, display);
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