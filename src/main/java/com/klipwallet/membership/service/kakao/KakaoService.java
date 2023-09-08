package com.klipwallet.membership.service.kakao;

import com.klipwallet.membership.entity.OpenChatting;
import com.klipwallet.membership.entity.OpenChattingMember;
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

    /**
     * 카카오 오픈채팅방 참여하기.
     *
     * @param member   참여자 정보
     * @param openChatting 채팅방 정보
     * @return 참여한 오픈채팅방 아이디 ValueObject
     */
    KakaoOpenlinkSummary joinOpenChatting(OpenChatting openChatting, OpenChattingMember member);
}
