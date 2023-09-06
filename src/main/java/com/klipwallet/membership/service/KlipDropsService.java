package com.klipwallet.membership.service;

import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsDrops;
import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsPartner;

public interface KlipDropsService {
    KlipDropsPartner getPartnerByBusinessRegistrationNumber(String businessRegistrationNumber);

    KlipDropsDrops getDropsByPartner(Integer klipDropsPartnerId);
}
