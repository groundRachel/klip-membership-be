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
import com.klipwallet.membership.repository.ChatRoomRepository;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final KakaoService kakaoService;
    private final ChatRoomAssembler chatRoomAssembler;

    @Transactional
    public ChatRoomSummary create(ChatRoomCreate command, AuthenticatedUser user) {
        // upload image to s3

        // get target id by command.hostId() and should have nickname, profileImage,

        // kakaoService.createOpenChatRoom(command.title(), command.coverImage(), )
        return null;
    }

    @Transactional(readOnly = true)
    public List<ChatRoomRow> getAllChatRooms() {
        List<ChatRoom> entities = chatRoomRepository.findAll();
        return chatRoomAssembler.toRows(entities);
    }
}
