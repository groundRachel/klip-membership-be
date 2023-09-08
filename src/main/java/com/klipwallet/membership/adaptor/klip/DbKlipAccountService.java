package com.klipwallet.membership.adaptor.klip;

import org.springframework.stereotype.Service;

import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.KlipUser;
import com.klipwallet.membership.entity.kakao.KakaoId;
import com.klipwallet.membership.service.KlipAccountService;

@Service
public class DbKlipAccountService implements KlipAccountService {
    @Override
    public KlipUser getKlipUser(Address klaytnAddress) {
        return null;
    }

    @Override
    public KlipUser getKlipUser(KakaoId kakaoId) {
        // FIXME @Jordan Fake
        if (kakaoId.getId().equals("2959264751")) {
            return new KlipAccount(641L, "2959264751", "jordan.jung@groundx.xyz", "01026383987");
        }
        return null;
    }

    @Override
    public KlipUser getKlipUserByPhoneNumber(String phoneNumber) {
        if ("01026383987".equals(phoneNumber)) {
            return new KlipAccount(641L, "2959264751", "jordan.jung@groundx.xyz", "01026383987");
        }
        return null;
    }
}
