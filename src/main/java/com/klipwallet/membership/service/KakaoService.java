package com.klipwallet.membership.service;

import com.klipwallet.membership.entity.kakao.OpenChatRoomHost;
import com.klipwallet.membership.entity.kakao.OpenChatRoomId;

public interface KakaoService {
    /**
     * 카카오 오픈채팅방 개설.
     *
     * @param title      오픈채팅방 제목
     * @param coverImage 오픈채팅방 커버 제목
     * @param host       오픈채팅방장
     * @return 개선될 오픈채팅방 아이디 ValueObject
     */
    OpenChatRoomId createOpenChatRoom(String title, String coverImage, OpenChatRoomHost host);
}
