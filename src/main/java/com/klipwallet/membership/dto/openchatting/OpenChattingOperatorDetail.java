package com.klipwallet.membership.dto.openchatting;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

import com.klipwallet.membership.entity.OpenChattingMember.Role;

public record OpenChattingOperatorDetail(
        @Schema(description = "오픈채팅방 멤버 Id", requiredMode = RequiredMode.REQUIRED, example = "1")
        Long id,
        @Schema(description = "파트너 계정 내 운영자 Id", requiredMode = RequiredMode.REQUIRED, example = "23")
        Long operatorId,
        @Schema(description = "오픈채팅방 운영자 닉네임", requiredMode = RequiredMode.REQUIRED, example = "운영자 1")
        String nickname,
        @Schema(description = "오픈채팅방 운영자 프로필 이미지", requiredMode = RequiredMode.REQUIRED,
                example = "https://klip-media.dev.klaytn.com/klip-membership/test.jpg")
        String profileImageUrl,
        @Schema(description = "오픈채팅방 운영자 역할", requiredMode = RequiredMode.REQUIRED, example = "host")
        Role role,
        @Schema(description = "오픈채팅방 운영자 이메일", requiredMode = RequiredMode.NOT_REQUIRED, example = "ian.han@groundx.xyz")
        String email
) {
}
