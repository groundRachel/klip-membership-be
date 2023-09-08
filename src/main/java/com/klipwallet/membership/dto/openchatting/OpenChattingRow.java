package com.klipwallet.membership.dto.openchatting;

import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.OpenChatting.Source;
import com.klipwallet.membership.entity.OpenChatting.Status;

/**
 * 채팅방 목록 행 DTO
 */
@Schema(description = "채팅방 목록 행 DTO")
public record OpenChattingRow(
        @Schema(description = "채팅방 ID") Long id,
        @Schema(description = "채팅방 제목", example = "Nyan-Cat Holder 모여라!") String title,
        @Schema(description = "NFT Contract 주소") Address contractAddress,
        Status status,
        Source source,
        Integer creatorId,
        @Schema(nullable = true) Integer updaterId,
        OffsetDateTime createdAt,
        @Schema(nullable = true) OffsetDateTime updatedAt) {
}
