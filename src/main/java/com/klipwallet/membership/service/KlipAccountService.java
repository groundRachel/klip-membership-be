package com.klipwallet.membership.service;

import jakarta.annotation.Nullable;

import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.KlipUser;
import com.klipwallet.membership.entity.kakao.KakaoId;

public interface KlipAccountService {
    /**
     * Klip 이용자 정보 조회
     *
     * @param klaytnAddress eoa
     * @return Klip 이용자 정보. 만약 존재하지 않으면 {@code null} 반환
     */
    @Nullable
    KlipUser getKlipUser(Address klaytnAddress);


    /**
     * Klip 이용자 정보 조회
     *
     * @param kakaoId 카카오유저아이디
     * @return Klip 이용자 정보. 만약 존재하지 않으면 {@code null} 반환
     */
    @Nullable
    KlipUser getKlipUser(KakaoId kakaoId);

    /**
     * Klip 이용자 정보 조회
     *
     * @param phoneNumber 휴대폰 번호(국가코드 제외)
     * @return Klip 이용자 정보. 만약 존재하지 않으면 {@code null} 반환
     */
    @Nullable
    KlipUser getKlipUserByPhoneNumber(String phoneNumber);
}
