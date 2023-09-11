package com.klipwallet.membership.dto.openchatting;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.NonNull;

import com.klipwallet.membership.adaptor.klipdrops.dto.DropStatus;
import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsDrop;
import com.klipwallet.membership.entity.OpenChattingNft;

public record OpenChattingNftDetail(
        @Schema(description = "오픈채팅방 NFT id", requiredMode = RequiredMode.REQUIRED, example = "1")
        Long id,

        @Schema(description = "Drop Id", requiredMode = RequiredMode.REQUIRED, example = "303890467")
        Long dropId,

        @Schema(description = "Drop 제목", example = "어둠 속의 예언 2023 디지털 포스터")
        String dropTitle,
        @Schema(description = "Drop 크리에이터 이름", example = "이우환")
        String dropCreatorName,
        @Schema(description = "Drop 발행 수", example = "1500")
        Integer dropTotalSupply,
        @Schema(description = "Drop 판매 수", example = "123")
        Integer dropTotalSalesCount,
        @Schema(description = "Drop 상태", example = "LIVE")
        DropStatus dropStatus
) {
    public OpenChattingNftDetail(@NonNull OpenChattingNft nft, @NonNull KlipDropsDrop drop) {
        this(nft.getId(), nft.getDropId(), drop.title(), drop.creatorName(), drop.totalSupply(), drop.totalSalesCount(), drop.status());
    }
}
