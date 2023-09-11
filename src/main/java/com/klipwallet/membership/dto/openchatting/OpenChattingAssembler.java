package com.klipwallet.membership.dto.openchatting;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsDrop;
import com.klipwallet.membership.dto.datetime.DateTimeAssembler;
import com.klipwallet.membership.entity.OpenChatting;
import com.klipwallet.membership.entity.OpenChattingNft;
import com.klipwallet.membership.service.KlipDropsService;

import static java.util.stream.Collectors.toMap;

@Component
@RequiredArgsConstructor
public class OpenChattingAssembler {
    private final OpenChattingMemberAssembler openChattingMemberAssembler;
    private final DateTimeAssembler dtAssembler;
    private final KlipDropsService klipDropsService;

    public Page<OpenChattingSummary> toSummaries(Page<OpenChatting> page) {
        return page.map(this::toSummary);
    }

    private OpenChattingSummary toSummary(OpenChatting entity) {
        return new OpenChattingSummary(entity.getId(), entity.getKakaoOpenlinkSummary().getId(), entity.getKakaoOpenlinkSummary().getUrl(),
                                       entity.getTitle(), entity.getStatus(),
                                       dtAssembler.toOffsetDateTime(entity.getCreatedAt()), dtAssembler.toOffsetDateTime(entity.getDeletedAt())
        );
    }

    public OpenChattingDetail toDetail(OpenChatting entity, List<OpenChattingNft> nftEntities) {
        OpenChattingOperatorDetail host = openChattingMemberAssembler.getHostDetail(entity.getId());
        List<OpenChattingOperatorDetail> operators = openChattingMemberAssembler.getOperatorsDetail(entity.getId());
        List<OpenChattingNftDetail> nfts = toNftDetails(nftEntities);
        return new OpenChattingDetail(entity.getId(), entity.getDescription(), entity.getCoverImage(), entity.getKakaoOpenlinkSummary().getId(),
                                      entity.getKakaoOpenlinkSummary().getUrl(),
                                      entity.getTitle(), entity.getStatus(), dtAssembler.toOffsetDateTime(entity.getCreatedAt()),
                                      dtAssembler.toOffsetDateTime(entity.getDeletedAt()), host, operators, nfts);
    }

    private List<OpenChattingNftDetail> toNftDetails(List<OpenChattingNft> nftEntities) {
        Set<Long> dropIds = nftEntities.stream().map(OpenChattingNft::getDropId).collect(Collectors.toUnmodifiableSet());
        List<KlipDropsDrop> drops = klipDropsService.getDropsByIds(dropIds);
        Map<Long, KlipDropsDrop> dropMap = drops.stream().collect(toMap(KlipDropsDrop::id, Function.identity()));
        return nftEntities.stream()
                          .map(n -> this.toOpenChattingNftDetail(n, dropMap))
                          .toList();
    }

    private OpenChattingNftDetail toOpenChattingNftDetail(OpenChattingNft nft, Map<Long, KlipDropsDrop> dropMap) {
        KlipDropsDrop klipDropsDrop = dropMap.getOrDefault(nft.getDropId(), KlipDropsDrop.EMPTY);
        return new OpenChattingNftDetail(nft, klipDropsDrop);
    }
}
