package com.klipwallet.membership.controller.error;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.NonNull;
import lombok.Value;
import org.springframework.validation.FieldError;

@Schema(description = "요청 본문 Field 오류 DTO", accessMode = AccessMode.READ_ONLY)
@Value
public class FieldErrorView {
    @Schema(description = "오류가 발생한 Field명", example = "title")
    String field;
    @Schema(description = "오류 내용", example = "제목은 크기가 1에서 200 사이여야 합니다.")
    String message;

    public FieldErrorView(@NonNull FieldError fieldError, @NonNull String resolvedMessage) {
        this.field = fieldError.getField();
        this.message = resolvedMessage;
    }
}
