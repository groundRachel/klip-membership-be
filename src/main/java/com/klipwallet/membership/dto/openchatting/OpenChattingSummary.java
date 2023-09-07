package com.klipwallet.membership.dto.openchatting;

import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅방 요약 DTO")
public record OpenChattingSummary(@Schema(description = "채팅방 ID", type = "string", example = "1") Long id,
                                  @Schema(description = "카카오 오픈채팅방 ID", example = "302067190") Long openChattingId,
                                  @Schema(description = "카카오 오픈채팅방 URL", example = "https://open.kakao.com/o/gvJWpNBf") String openChattingUrl,
                                  @Schema(description = "채팅방 제목", example = "Nyan-Cat Holder 모여라!") String title,
                                  @Schema(description = "생성자 ID", example = "2776") Integer creatorId,
                                  @Schema(description = "생성일시", example = "2023-07-24T15:38:24.005795+09:00") OffsetDateTime createdAt) {
}
