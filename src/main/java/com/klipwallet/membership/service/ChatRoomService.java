package com.klipwallet.membership.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.chatroom.ChatRoomAssembler;
import com.klipwallet.membership.dto.chatroom.ChatRoomCreate;
import com.klipwallet.membership.dto.chatroom.ChatRoomRow;
import com.klipwallet.membership.dto.chatroom.ChatRoomSummary;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.ChatRoom;
import com.klipwallet.membership.entity.kakao.OpenChatRoomHost;
import com.klipwallet.membership.entity.kakao.OpenChatRoomId;
import com.klipwallet.membership.exception.NeedLinkToKakaoException;
import com.klipwallet.membership.repository.ChatRoomRepository;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final KakaoService kakaoService;
    private final ChatRoomAssembler chatRoomAssembler;

    @Transactional
    public ChatRoomSummary create(ChatRoomCreate command, AuthenticatedUser user) {
        // FIXME Member 계정에 카카오 연동이 되었는가? AOP로 처리할까?
        if (!user.isLinkedToKakao()) {
            throw new NeedLinkToKakaoException(user);
        }
        // 카카오 오픈채팅방 생성
        OpenChatRoomId openChatRoomId = kakaoService.createOpenChatRoom(
                command.title(), command.coverImage(), new OpenChatRoomHost(user.getKakaoId()));
        // 채팅방 저장
        ChatRoom entity = command.toChatRoom(openChatRoomId, user.getMemberId());
        return chatRoomAssembler.toSummary(chatRoomRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<ChatRoomRow> getAllChatRooms() {
        List<ChatRoom> entities = chatRoomRepository.findAll();
        return chatRoomAssembler.toRows(entities);
    }
}
