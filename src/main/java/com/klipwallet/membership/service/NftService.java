package com.klipwallet.membership.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsDrops;
import com.klipwallet.membership.dto.nft.NftAssembler;
import com.klipwallet.membership.dto.nft.NftDto.NftSummary;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.exception.member.KlipDropsPartnerIdNotFound;
import com.klipwallet.membership.exception.member.PartnerNotFoundException;
import com.klipwallet.membership.repository.PartnerRepository;

@Service
@RequiredArgsConstructor
public class NftService {
    private final PartnerRepository partnerRepository;
    private final KlipDropsService klipDropsService;
    private final NftAssembler nftAssembler;

    @Transactional(readOnly = true)
    public List<NftSummary> getNftList(MemberId partnerId) {
        Partner partner = partnerRepository.findById(partnerId.value())
                                           .orElseThrow(() -> new PartnerNotFoundException(partnerId));

        Integer klipDropsPartnerId = partner.getKlipDropsPartnerId();
        if (klipDropsPartnerId == null) {
            throw new KlipDropsPartnerIdNotFound(partnerId);
        }

        // TODO: 다른 채팅에 등록된 Drop은 filter out
        
        KlipDropsDrops dropsByPartner = klipDropsService.getDropsByPartner(klipDropsPartnerId);
        return nftAssembler.toNftSummary(dropsByPartner);
    }
}
