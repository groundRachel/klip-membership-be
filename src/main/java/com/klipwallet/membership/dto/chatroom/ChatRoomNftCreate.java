package com.klipwallet.membership.dto.chatroom;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

import com.klipwallet.membership.entity.Address;

public record ChatRoomNftCreate(
        @Schema(description = "Drop id")
        @NotNull
        Long dropId,
        @Schema(description = "Drop SCA")
        @NotNull
        Address sca
) {
}
