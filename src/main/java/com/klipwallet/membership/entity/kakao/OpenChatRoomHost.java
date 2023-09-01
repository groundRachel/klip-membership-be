package com.klipwallet.membership.entity.kakao;

import lombok.Value;

/**
 * 카카오 오픈채팅방장 ValueObject
 */
@SuppressWarnings("ClassCanBeRecord")
@Value
public class OpenChatRoomHost {
    KakaoId kakaoId;
    String nickname;
    String profileImageUrl;
}
