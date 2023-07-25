package com.klipwallet.membership.dto.chatroom;

import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.ChatRoom.Source;
import com.klipwallet.membership.entity.ChatRoom.Status;

/**
 * 채팅방 목록 행 DTO
 */
@Schema(description = "채팅방 목록 행 DTO")
public record ChatRoomRow(
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
