package com.klipwallet.membership.service;

import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.KlipUser;
import com.klipwallet.membership.entity.kakao.KakaoId;

public interface KlipAccountService {
    KlipUser getKlipUser(Address klaytnAddress);

    KlipUser getKlipUser(KakaoId kakaoId);

    KlipUser getKlipUserByPhoneNumber(String phoneNumber);
}
