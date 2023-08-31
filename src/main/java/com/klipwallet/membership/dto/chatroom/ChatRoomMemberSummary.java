package com.klipwallet.membership.dto.chatroom;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.NonNull;

import com.klipwallet.membership.entity.ChatRoomMember;

@Schema(description = "채팅방 멤버 요약 DTO", accessMode = AccessMode.READ_ONLY)
public record ChatRoomMemberSummary(
        @NonNull @Schema(description = "채팅방 멤버 ID", type = "string", example = "1") Long id,
        @NonNull @Schema(description = "채팅방 멤버 이메일", type = "string", example = "testemail@gmail.com") String email,
        @NonNull @Schema(description = "채팅방 멤버 전화번호", type = "string", example = "010-1234-5678") String phone,
        @NonNull @Schema(description = "채팅방 멤버 닉네임", type = "string", example = "testnickname") String nickname,
        @NonNull @Schema(description = "채팅방 멤버 프로필 이미지 URL", type = "string",
                         example = "https://exampleimage.com/klip-membership/1/1234") String profileImageUrl
) {
    public ChatRoomMemberSummary(ChatRoomMember saved) {
        this(saved.getId(),
             "", "",
             saved.getNickname(),
             saved.getProfileImageUrl());
    }
}
