package com.klipwallet.membership.dto.openchatting;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

import com.klipwallet.membership.entity.OpenChattingMember;

import static com.klipwallet.membership.entity.OpenChattingMember.Role.NFT_HOLDER;

@Schema(description = "채팅방 멤버 생성 DTO", accessMode = AccessMode.WRITE_ONLY)
public record OpenChattingMemberCreate(
        @Schema(description = "requestKey")
        @NotBlank
        String klipRequestKey,
        @Schema(description = "오픈채팅방 멤버 닉네임")
        @Size(max = 20)
        String nickname,
        @Schema(description = "오픈채팅방 멤버 프로필 이미지")
        String profileImageUrl
) {
    @JsonIgnore
    public OpenChattingMember toOpenChattingMember(Long chatRoomId, Long klipId, String kakaoUserId) {
        return new OpenChattingMember(chatRoomId, klipId, kakaoUserId, 0L, nickname, profileImageUrl, NFT_HOLDER);
    }
}
