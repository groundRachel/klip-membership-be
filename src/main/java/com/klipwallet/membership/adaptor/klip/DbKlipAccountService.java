package com.klipwallet.membership.adaptor.klip;

import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.KlipUser;
import com.klipwallet.membership.entity.kakao.KakaoId;
import com.klipwallet.membership.service.KlipAccountService;

// FIXME Implementation
public class DbKlipAccountService implements KlipAccountService {
    @Override
    public KlipUser getKlipUser(Address klaytnAddress) {
        return null;
    }

    @Override
    public KlipUser getKlipUser(KakaoId kakaoId) {
        return null;
    }

    @Override
    public KlipUser getKlipUserByPhoneNumber(String phoneNumber) {
        return null;
    }
}
