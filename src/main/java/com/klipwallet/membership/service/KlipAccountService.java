package com.klipwallet.membership.service;

import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.KlipUser;

public interface KlipAccountService {
    Address getKlaytnAddress(String requestKey);

    KlipUser getKlipUser(Address klaytnAddress);
}
