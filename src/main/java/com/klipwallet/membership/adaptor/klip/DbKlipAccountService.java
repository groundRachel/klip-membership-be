package com.klipwallet.membership.adaptor.klip;

import org.springframework.stereotype.Service;

import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.service.KlipAccountService;

@Service
public class DbKlipAccountService implements KlipAccountService {
    @Override
    public Address getKlaytnAddress(String requestKey) {
        // TODO: @Ian
        return null;
    }

    @Override
    public KlipAccount getKlipUser(Address klaytnAddress) {
        return null;
    }
}
