package com.klipwallet.membership.adaptor.klipdrops;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsDrops;
import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsPartner;
import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsPartners;
import com.klipwallet.membership.service.KlipDropsService;

@Component
@RequiredArgsConstructor
public class KlipDropsAdaptor implements KlipDropsService {
    private final KlipDropsInternalApiClient klipDropsInternalApiClient;

    @Override
    public List<KlipDropsPartner> getAllPartners(String search) {
        String cursor = "";
        Integer size = 1000;
        return klipDropsInternalApiClient.getAllPartners(null, null, search, cursor, size).klipDropsPartners();
    }

    @Override
    public KlipDropsPartner getPartnerById(Integer partnerId) {
        Integer size = 1;
        KlipDropsPartners allPartners = klipDropsInternalApiClient.getAllPartners(partnerId, null, "", "", size);
        if (allPartners == null || allPartners.klipDropsPartners().isEmpty()) {
            return null;
        }
        return allPartners.klipDropsPartners().get(0);
    }

    @Override
    public KlipDropsPartner getPartnerByBusinessRegistrationNumber(String businessRegistrationNumber) {
        Integer size = 1;
        KlipDropsPartners allPartners = klipDropsInternalApiClient.getAllPartners(null, businessRegistrationNumber, null, null, size);
        if (allPartners == null || allPartners.klipDropsPartners().isEmpty()) {
            return null;
        }
        return allPartners.klipDropsPartners().get(0);
    }

    @Override
    public KlipDropsDrops getDropsByPartner(Integer klipDropsPartnerId) {
        Integer page = 1;
        Integer size = 1000;
        return klipDropsInternalApiClient.getDropsByPartner(klipDropsPartnerId, page, size);
    }
}
