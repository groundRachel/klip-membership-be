package com.klipwallet.membership.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsPartner;
import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsTranslator;
import com.klipwallet.membership.dto.klipdrops.KlipDropsDto.Partner;
import com.klipwallet.membership.repository.PartnerRepository;

@Service
@RequiredArgsConstructor
public class KlipDropsPartnerService {
    private final KlipDropsService klipDropsService;
    private final PartnerRepository partnerRepository;
    private final KlipDropsTranslator klipDropsTranslator;

    @Transactional(readOnly = true)
    public List<Partner> getKlipDropsPartners(String search) {
        List<KlipDropsPartner> klipDropsPartners = klipDropsService.getAllPartners(search);
        if (klipDropsPartners == null || klipDropsPartners.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Integer> klipDropsIdsFromRepo = partnerRepository.findAllKlipDropsIds();
        if (klipDropsIdsFromRepo == null || klipDropsIdsFromRepo.isEmpty()) {
            return klipDropsTranslator.toPartners(klipDropsPartners);
        }

        List<KlipDropsPartner> unusedPartners = filterUnusedPartners(klipDropsPartners, klipDropsIdsFromRepo);
        return klipDropsTranslator.toPartners(unusedPartners);
    }

    private List<KlipDropsPartner> filterUnusedPartners(List<KlipDropsPartner> klipDropsPartners, Set<Integer> klipDropsIdsFromRepo) {
        List<KlipDropsPartner> unusedPartners = new LinkedList<>();

        for (KlipDropsPartner klipDropsPartner : klipDropsPartners) {
            Integer fetchedId = klipDropsPartner.partnerId();
            if (!klipDropsIdsFromRepo.contains(fetchedId)) {
                unusedPartners.add(klipDropsPartner);
            }
        }
        return unusedPartners;
    }
}
