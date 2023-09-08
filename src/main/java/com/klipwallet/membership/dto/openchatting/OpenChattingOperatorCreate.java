package com.klipwallet.membership.dto.openchatting;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

import com.klipwallet.membership.entity.OpenChatting;
import com.klipwallet.membership.entity.OpenChattingMember;
import com.klipwallet.membership.entity.OpenChattingMember.Role;
import com.klipwallet.membership.entity.Operator;

public record OpenChattingOperatorCreate(
        @Schema(description = "운영자 Id")
        @NotNull
        Long operatorId,
        @Schema(description = "운영자 채팅방 닉네임")
        @NotBlank @Size(max = 20)
        String nickname,
        @Schema(description = "운영자 채팅방 프로필 이미지")
        @NotBlank
        String profileImageUrl
) {
    @JsonIgnore
    public OpenChattingMember toOpenChattingMember(OpenChatting openChatting, Operator operator, Role role) {
        return new OpenChattingMember(openChatting.getId(), operator.getKlipId(), operator.getKakaoUserId(), operatorId, nickname, profileImageUrl,
                                      role);
    }
}
