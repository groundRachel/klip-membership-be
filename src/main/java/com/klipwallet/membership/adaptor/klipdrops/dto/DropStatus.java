package com.klipwallet.membership.adaptor.klipdrops.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.lang.Nullable;

import com.klipwallet.membership.entity.Statusable;

@Getter
@Schema(name = "KlipDrops.DropStatus", description = "드롭 상태", example = "onSale")
public enum DropStatus implements Statusable {
    ON_SALE(1),
    SCHEDULED_FOR_SALE(2),
    END_OF_SALE(3),
    STOP_SALE(4),
    INACTIVE_DROP(5);

    private final byte code;

    DropStatus(int code) {
        this.code = Statusable.requireVerifiedCode(code);
    }

    @JsonCreator
    @Nullable
    public static DropStatus fromDisplay(String display) {
        return Statusable.fromDisplay(DropStatus.class, display);
    }

    @JsonValue
    @Override
    public String toDisplay() {
        return Statusable.super.toDisplay();
    }
}
