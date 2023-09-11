package com.klipwallet.membership.dto.nft;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.RequiredArgsConstructor;

import com.klipwallet.membership.adaptor.klipdrops.dto.DropStatus;

@RequiredArgsConstructor
public class NftDto {
    @Schema(description = "[Tool] 오픈채팅방 생성을 위한 NFT 리스트 조회", accessMode = AccessMode.READ_ONLY)
    public record Summary(
            String name,
            String creatorName,
            Long dropId,
            Integer totalSalesCount,
            Integer totalSupply,
            DropStatus status
    ) {
    }
}
