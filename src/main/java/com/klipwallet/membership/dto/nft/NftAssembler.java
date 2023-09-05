package com.klipwallet.membership.dto.nft;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsDrops;
import com.klipwallet.membership.dto.nft.NftDto.NftSummary;

@Component
@RequiredArgsConstructor
public class NftAssembler {
    public List<NftSummary> toNftSummary(KlipDropsDrops klipDropsDrops) {
        return klipDropsDrops.drops().stream()
                             .map(drop -> new NftSummary(drop.title(), drop.creatorName(), drop.id(), drop.totalSalesCount(), drop.totalSupply(),
                                                         drop.status())).toList();
    }
}
