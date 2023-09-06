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
        return new KlipAccount(641L, kakaoId.getId(), "jordan.jung@groundx.xyz", "01026382580");
    }
}
