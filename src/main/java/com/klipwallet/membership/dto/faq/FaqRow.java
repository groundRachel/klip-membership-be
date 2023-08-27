package com.klipwallet.membership.dto.faq;

import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

import com.klipwallet.membership.dto.member.MemberSummary;
import com.klipwallet.membership.entity.ArticleStatus;

@Schema(description = "FAQ 목록의 Row DTO", accessMode = AccessMode.READ_ONLY)
public record FaqRow(
        @Schema(description = "FAQ ID", example = "1")
        Integer id,
        @Schema(description = "제목", minLength = 1, maxLength = 200, example = "멤버십 툴에 어떻게 가입하나요?")
        String title,
        @Schema(description = "상태", example = "live")
        ArticleStatus status,
        @Schema(description = "최근 Live 일시", example = "2023-07-24T15:38:24.005795+09:00")
        OffsetDateTime livedAt,
        @Schema(description = "생성일시", example = "2023-07-24T15:38:24.005795+09:00")
        OffsetDateTime createdAt,
        @Schema(description = "마지막 수정일시", example = "2023-07-24T15:38:24.005795+09:00")
        OffsetDateTime updatedAt,
        @Schema(description = "생성자")
        MemberSummary creator,
        @Schema(description = "마지막 수정자")
        MemberSummary updater
) {
}
