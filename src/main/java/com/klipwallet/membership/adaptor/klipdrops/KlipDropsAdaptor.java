package com.klipwallet.membership.adaptor.klipdrops;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsPartner;
import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsPartners;
import com.klipwallet.membership.service.KlipDropsService;

@Component
@RequiredArgsConstructor
public class KlipDropsAdaptor implements KlipDropsService {
    private final KlipDropsInternalApiClient klipDropsInternalApiClient;

    @Override
    public KlipDropsPartner getPartnerByBusinessRegistrationNumber(String businessRegistrationNumber) {
        Integer size = 1;
        KlipDropsPartners allPartners = klipDropsInternalApiClient.getAllPartners(businessRegistrationNumber, null, null, size);
        return allPartners.klipDropsPartners().get(0);
    }
}
