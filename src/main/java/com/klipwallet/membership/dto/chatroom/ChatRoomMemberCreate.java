package com.klipwallet.membership.dto.chatroom;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

import com.klipwallet.membership.entity.ChatRoom;
import com.klipwallet.membership.entity.ChatRoomMember;
import com.klipwallet.membership.entity.ChatRoomMember.Role;

@Schema(description = "채팅방 멤버 생성 DTO", accessMode = AccessMode.WRITE_ONLY)
public record ChatRoomMemberCreate(
        @Schema(description = "클립 회원 Id")
        @NotNull
        Long klipId,
        @Schema(description = "카카오 유저 Id")
        @NotBlank
        String kakaoUserId,

        /**
         * 연결된 운영자 Id 참조
         * {@link com.klipwallet.membership.entity.Operator#getId()}
         */
        @Schema(description = "운영자 Id")
        Long operatorId,
        @Schema(description = "오픈채팅방 멤버 닉네임")
        @NotBlank
        String nickname,
        @Schema(description = "오픈채팅방 멤버 프로필 이미지")
        @NotBlank
        String profileImageUrl,
        @Schema(description = "오픈 채팅방 멤버 역할")
        @NotNull
        Role role
) {
    @JsonIgnore
    public ChatRoomMember toChatRoomMember(ChatRoom chatRoom) {
        return new ChatRoomMember(chatRoom.getId(), klipId, kakaoUserId, operatorId, nickname, profileImageUrl, role);
    }
}
