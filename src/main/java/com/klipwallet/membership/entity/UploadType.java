package com.klipwallet.membership.entity;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.util.unit.DataSize;

@Getter
@Schema(name = "UploadType", description = "업로드 타입", example = "profile")
public enum UploadType implements Statusable {
    /**
     * WYSIWYG 에디터 이미지
     */
    @Hidden
    EDITOR(1, DataSize.ofMegabytes(2)),
    /**
     * 오픈채팅 커버 이미지
     */
    COVER(2, DataSize.ofKilobytes(400)),
    /**
     * 오픈채팅 참여자 프로필 이미지
     */
    PROFILE(3, DataSize.ofMegabytes(10));


    private final byte code;
    private final DataSize limit;

    UploadType(int code, DataSize limit) {
        this.code = Statusable.requireVerifiedCode(code);
        this.limit = limit;
    }

    @JsonCreator
    @Nullable
    public static UploadType fromDisplay(String display) {
        return Statusable.fromDisplay(UploadType.class, display);
    }

    @JsonValue
    @Override
    public String toDisplay() {
        return Statusable.super.toDisplay();
    }
}
