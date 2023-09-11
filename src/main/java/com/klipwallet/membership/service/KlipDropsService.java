package com.klipwallet.membership.service;

import java.util.List;

import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsDrops;
import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsPartner;

public interface KlipDropsService {
    List<KlipDropsPartner> getAllPartners(String search);

    KlipDropsPartner getPartnerById(Integer partnerId);

    KlipDropsPartner getPartnerByBusinessRegistrationNumber(String businessRegistrationNumber);

    KlipDropsDrops getDropsByPartner(Integer klipDropsPartnerId);
}
