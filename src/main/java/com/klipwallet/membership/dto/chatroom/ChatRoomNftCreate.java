package com.klipwallet.membership.dto.chatroom;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.ChatRoomNft;
import com.klipwallet.membership.entity.MemberId;

public record ChatRoomNftCreate(
        @Schema(description = "Drop id")
        @NotNull
        Long dropId,
        @Schema(description = "Drop SCA")
        @NotNull
        Address sca
) {
    public ChatRoomNft toChatRoomNft(Integer partnerId, Long chatRoomId, MemberId creatorId) {
        return new ChatRoomNft(partnerId, chatRoomId, dropId, sca, creatorId);
    }
}
