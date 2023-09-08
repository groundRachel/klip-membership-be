package com.klipwallet.membership.dto.openchatting;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.OpenChattingNft;

public record OpenChattingNftCreate(
        @Schema(description = "Drop id")
        @NotNull
        Long dropId,
        @Schema(description = "Drop SCA")
        @NotNull
        Address klipDropsSca
) {
    public OpenChattingNft toOpenChattingNft(Long openChattingId, MemberId creatorId) {
        return new OpenChattingNft(openChattingId, dropId, klipDropsSca, creatorId);
    }
}
