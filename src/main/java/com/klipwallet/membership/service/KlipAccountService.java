package com.klipwallet.membership.service;

import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.KlipUser;

public interface KlipAccountService {
    KlipUser getKlipUser(Address klaytnAddress);
}
