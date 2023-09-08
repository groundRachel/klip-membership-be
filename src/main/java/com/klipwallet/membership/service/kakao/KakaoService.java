package com.klipwallet.membership.service.kakao;

import com.klipwallet.membership.entity.kakao.KakaoOpenlinkSummary;
import com.klipwallet.membership.entity.kakao.OpenChattingHost;

public interface KakaoService {
    /**
     * 카카오 오픈채팅방 개설.
     *
     * @param title       오픈채팅방 제목
     * @param description
     * @param coverImage  오픈채팅방 커버 제목
     * @param host        오픈채팅방장
     * @return 개선될 오픈채팅방 아이디 ValueObject
     */
    KakaoOpenlinkSummary createOpenChatting(String title, String description, String coverImage, OpenChattingHost host);

    KakaoOpenlinkSummary joinOpenChatting(Long linkId, String nickname, String profileImage, String targetId);
}
