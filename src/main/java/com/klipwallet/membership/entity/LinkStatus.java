package com.klipwallet.membership.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
@Schema(name = "LinkStatus", description = "첨부파일 연결 상태", example = "unlink")
public enum LinkStatus implements Statusable {
    UNLINK(0),
    LINKED(1);

    private final byte code;

    LinkStatus(int code) {
        this.code = Statusable.requireVerifiedCode(code);
    }

    @JsonCreator
    @Nullable
    public static LinkStatus fromDisplay(String display) {
        return Statusable.fromDisplay(LinkStatus.class, display);
    }

    @JsonValue
    @Override
    public String toDisplay() {
        return Statusable.super.toDisplay();
    }
}
