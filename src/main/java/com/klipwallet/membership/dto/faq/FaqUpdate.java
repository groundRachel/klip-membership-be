package com.klipwallet.membership.dto.faq;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.Faq.Status;

@Schema(description = "FAQ 수정 DTO", accessMode = AccessMode.WRITE_ONLY)
public record FaqUpdate(
        @Schema(description = "제목", minLength = 1, maxLength = 200, example = "멤버십 툴에 어떻게 가입하나요?")
        @NotBlank @Size(min = 1, max = 200)
        String title,
        @Schema(description = "본문", example = "<p>GX 파트너는 누구나 가입할 수 있습니다.</p>")
        @NotBlank
        String body,
        @Schema(description = "FAQ 상태")
        Status status) {

    @com.fasterxml.jackson.annotation.JsonIgnore
    public com.klipwallet.membership.entity.FaqUpdatable toUpdatable(AuthenticatedUser user) {
        return new FaqUpdatable(this.title(), this.body(), this.status(), user.getMemberId());
    }
}