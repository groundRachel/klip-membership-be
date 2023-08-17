package com.klipwallet.membership.dto.faq;

import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

import com.klipwallet.membership.dto.member.MemberSummary;
import com.klipwallet.membership.entity.Faq.Status;

@Schema(description = "FAQ 상세 DTO", accessMode = AccessMode.READ_ONLY)
public record FaqDetail(
        @Schema(description = "FAQ ID", example = "1")
        Integer id,
        @Schema(description = "제목", minLength = 1, maxLength = 200, example = "멤버십 툴에 어떻게 가입하나요?")
        String title,
        @Schema(description = "본문", example = "<p>GX 파트너는 누구나 가입할 수 있습니다.</p>")
        String body,
        @Schema(description = "상태", example = "live")
        Status status,
        @Schema(description = "생성일시", example = "2023-07-24T15:38:24.005795+09:00")
        OffsetDateTime createdAt,
        @Schema(description = "마지막 수정일시", example = "2023-07-24T15:38:24.005795+09:00")
        OffsetDateTime updatedAt,
        @Schema(description = "생성자")
        MemberSummary creator,
        @Schema(description = "마지막 수정자")
        MemberSummary updater
){
}