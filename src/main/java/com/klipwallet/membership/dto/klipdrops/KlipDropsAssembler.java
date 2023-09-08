package com.klipwallet.membership.dto.klipdrops;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsPartner;
import com.klipwallet.membership.dto.klipdrops.KlipDropsDto.Partner;

@Component
@RequiredArgsConstructor
public class KlipDropsAssembler {

    public List<Partner> toPartners(List<KlipDropsPartner> klipDropsPartners) {
        return klipDropsPartners.stream()
                                .map(this::toPartner)
                                .toList();
    }

    public Partner toPartner(KlipDropsPartner klipDropsPartner) {
        return new Partner(klipDropsPartner.partnerId(),
                           klipDropsPartner.name(),
                           klipDropsPartner.businessRegistrationNumber());
    }
}
