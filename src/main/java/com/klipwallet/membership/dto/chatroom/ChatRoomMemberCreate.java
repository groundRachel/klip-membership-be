package com.klipwallet.membership.dto.chatroom;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

import com.klipwallet.membership.entity.ChatRoom;
import com.klipwallet.membership.entity.ChatRoomMember;
import com.klipwallet.membership.entity.ChatRoomMember.Role;
import com.klipwallet.membership.entity.KlipUser;

@Schema(description = "채팅방 멤버 생성 DTO", accessMode = AccessMode.WRITE_ONLY)
public record ChatRoomMemberCreate(
        @Schema(description = "클립 회원 Id")
        @NotNull
        Long klipId,
        @Schema(description = "오픈채팅방 Id")
        Long chatRoomId,
        @Schema(description = "오픈채팅방 멤버 닉네임")
        @NotBlank
        String nickname,
        @Schema(description = "오픈채팅방 멤버 프로필 이미지")
        @NotBlank
        String profileImageUrl,
        @Schema(description = "오픈 채팅방 멤버 역할")
        @NotNull
        Role role

        // TODO: Add Operator Id (Optional)
) {
    @JsonIgnore
    public ChatRoomMember toChatRoomMember(KlipUser klipUser, ChatRoom chatRoom) {
        return new ChatRoomMember(klipUser.getKlipAccountId(), klipUser.getKakaoUserId(), klipUser.getEmail(), klipUser.getPhone(),
                                  nickname, profileImageUrl, role);
    }
}
