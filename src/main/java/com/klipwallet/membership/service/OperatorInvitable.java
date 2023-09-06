package com.klipwallet.membership.service;

import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.OperatorInvitation;

public interface OperatorInvitable {
    /**
     * 운영진 초대
     * <p>
     * 1. 휴대폰 번호로 운영진 초대 링크(24시간 만료)를 카카오 알림톡으로 발송<br/>
     * 2. 카카오 인증(인톡 클립이어서 SKIP)
     * 3. 카카오 오픈 채팅 권한 승인(OAuth2 권한에 포함)
     * 4. 이용 동의
     * 5. 초대 완료(kakaoUserId)
     * </p>
     *
     * @param partnerId   운영진을 관리하는 파트너 아이디
     * @param phoneNumber 휴대폰 번호
     * @return 초대 링크
     */
    String inviteOperator(MemberId partnerId, String phoneNumber);

    /**
     * 이미 운영진인가?
     *
     * @param kakaoUserId 카카오 아이디
     * @return 운영지 가입 여부
     */
    boolean isAlreadyOperator(String kakaoUserId);

    /**
     * 초대 코드로 초대 정보 조회
     *
     * @param invitationCode 초대 코드
     * @return 초대 정보
     */
    OperatorInvitation lookup(String invitationCode);
}
