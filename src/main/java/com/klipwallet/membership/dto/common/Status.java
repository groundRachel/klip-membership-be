package com.klipwallet.membership.dto.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.github.resilience4j.core.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import com.klipwallet.membership.entity.Faq;
import com.klipwallet.membership.entity.Statusable;

@Getter
@Schema(name = "Status", description = "상태", example = "success")
public enum Status implements Statusable {
    SUCCESS(0),
    FAIL(1);

    private final byte code;

    Status(int code) {
        this.code = Statusable.requireVerifiedCode(code);
    }

    @JsonCreator
    @Nullable
    public static Faq.Status fromDisplay(String display) {
        return Statusable.fromDisplay(Faq.Status.class, display);
    }

    @JsonValue
    @Override
    public String toDisplay() {
        return Statusable.super.toDisplay();
    }
}
