package com.klipwallet.membership.dto.chatroom;

import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

import com.klipwallet.membership.entity.kakao.OpenChatRoomId;

@Schema(description = "채팅방 요약 DTO")
public record ChatRoomSummary(@Schema(description = "채팅방 ID", type = "string", example = "1") Long id,
                              @Schema(description = "카카오 오픈채팅방 ID", example = "2ccfdfa9-d3a8-4a53-af45-e45a14f45b05") OpenChatRoomId openChatRoomId,
                              @Schema(description = "채팅방 제목", example = "Nyan-Cat Holder 모여라!") String title,
                              @Schema(description = "생성자 ID", example = "2776") Integer creatorId,
                              @Schema(description = "생성일시", example = "2023-07-24T15:38:24.005795+09:00") OffsetDateTime createdAt) {
}
