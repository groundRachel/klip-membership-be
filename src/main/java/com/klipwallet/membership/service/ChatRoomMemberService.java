package com.klipwallet.membership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.klipwallet.membership.dto.chatroom.ChatRoomMemberCreate;
import com.klipwallet.membership.dto.chatroom.ChatRoomMemberSummary;
import com.klipwallet.membership.entity.ChatRoom;
import com.klipwallet.membership.entity.ChatRoomMember;
import com.klipwallet.membership.entity.ChatRoomMember.Role;
import com.klipwallet.membership.entity.KlipUser;
import com.klipwallet.membership.repository.ChatRoomMemberRepository;
import com.klipwallet.membership.repository.ChatRoomRepository;

@Service
@RequiredArgsConstructor
public class ChatRoomMemberService {
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final KlipAccountService klipAccountService;

    public ChatRoomMemberSummary create(ChatRoomMemberCreate command) {
        if (command.role() == Role.NFT_HOLDER) {
            // TODO: Check NFT
        } else if (command.role() == Role.OPERATOR) {
            // TODO: Check num of operator
        }
        ChatRoom chatRoom = getChatRoom(command.chatRoomId());

        KlipUser klipUser = klipAccountService.getKlipId();

        ChatRoomMember entity = command.toChatRoomMember(klipUser, chatRoom);
        ChatRoomMember saved = chatRoomMemberRepository.save(entity);
        return new ChatRoomMemberSummary(saved);
    }

    public ChatRoom getChatRoom(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).get();
    }
}
