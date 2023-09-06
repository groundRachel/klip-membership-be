package com.klipwallet.membership.dto.nft;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsDrop;
import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsDrops;
import com.klipwallet.membership.dto.nft.NftDto.Summary;

@Component
@RequiredArgsConstructor
public class NftAssembler {
    public List<Summary> toNftSummaries(KlipDropsDrops klipDropsDrops) {
        return klipDropsDrops.drops().stream()
                             .map(this::toNftSummary)
                             .toList();
    }

    private Summary toNftSummary(KlipDropsDrop drop) {
        return new Summary(drop.title(),
                           drop.creatorName(),
                           drop.id(),
                           drop.totalSalesCount(),
                           drop.totalSupply(),
                           drop.status());
    }
}
