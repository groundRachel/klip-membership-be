package com.klipwallet.membership.dto.openchatting;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "채팅방 상태 DTO", accessMode = AccessMode.READ_ONLY)
public record OpenChattingStatus(
        @Schema(description = "카카오 오픈채팅방 존재 여부", requiredMode = REQUIRED, example = "true") boolean existOpenChatting,
        @Schema(description = "카카오 오픈채팅방 url", requiredMode = NOT_REQUIRED, example = "https://open.kakao.com/o/gvJWpNBf") String openChattingUrl,
        @Schema(description = "해당 오픈채팅방에 프로필이 존재하는가", requiredMode = REQUIRED, example = "true") boolean hasProfile) {
}
