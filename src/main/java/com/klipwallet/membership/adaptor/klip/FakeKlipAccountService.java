package com.klipwallet.membership.adaptor.klip;

import java.util.List;

import org.springframework.stereotype.Service;

import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.KlipUser;
import com.klipwallet.membership.entity.kakao.KakaoId;
import com.klipwallet.membership.service.KlipAccountService;

@Service
public class FakeKlipAccountService implements KlipAccountService {
    private final List<KlipAccount> accounts;

    public FakeKlipAccountService() {
        accounts = List.of(
                new KlipAccount(529L, "2959264750", "redutan@gmail.com", "01026383987"),    // Jordan
                new KlipAccount(641L, "2526366863", "sally4405@naver.com", "01044334405")   // Sello
        );
    }

    @Override
    public KlipUser getKlipUser(Address klaytnAddress) {
        return null;
    }

    @Override
    public KlipUser getKlipUser(KakaoId kakaoId) {
        // FIXME @Jordan Fake
        return accounts.stream().filter(a -> a.getKlipAccountId().toString().equals(kakaoId.getId())).findFirst().orElse(null);
    }

    @Override
    public KlipUser getKlipUserByPhoneNumber(String phoneNumber) {
        return accounts.stream().filter(a -> a.getPhone().equals(phoneNumber)).findFirst().orElse(null);
    }
}
