package com.klipwallet.membership.dto.faq;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import net.minidev.json.annotate.JsonIgnore;

import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.Faq;

@Schema(description = "FAQ 생성 DTO", accessMode = AccessMode.WRITE_ONLY)
public record FaqCreate(
        @Schema(description = "제목", minLength = 1, maxLength = 200, example = "멤버십 툴에 어떻게 가입하나요?")
        @NotBlank @Size(min = 1, max = 200)
        String title,
        @Schema(description = "본문", example = "<p>GX 파트너는 누구나 가입할 수 있습니다.</p>")
        @NotBlank
        String body) {
    @JsonIgnore
    public Faq toFAQ(AuthenticatedUser user) {
        return new Faq(title, body, user.getMemberId());
    }
}
