package com.klipwallet.membership.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.chatroom.ChatRoomAssembler;
import com.klipwallet.membership.dto.chatroom.ChatRoomCreate;
import com.klipwallet.membership.dto.chatroom.ChatRoomOperatorCreate;
import com.klipwallet.membership.dto.chatroom.ChatRoomRow;
import com.klipwallet.membership.dto.chatroom.ChatRoomSummary;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.ChatRoom;
import com.klipwallet.membership.entity.ChatRoomMember;
import com.klipwallet.membership.entity.ChatRoomMember.Role;
import com.klipwallet.membership.exception.ChatRoomExceedOperatorLimitException;
import com.klipwallet.membership.exception.kakao.OperatorAndHostHaveSameId;
import com.klipwallet.membership.repository.ChatRoomRepository;
import com.klipwallet.membership.repository.OperatorRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {
    private static final int OPERATOR_SIZE_LIMIT = 4;
    private final ChatRoomRepository chatRoomRepository;
    private final KakaoService kakaoService;
    private final ChatRoomAssembler chatRoomAssembler;
    private final OperatorRepository operatorRepository;
    private final ChatRoomMemberService chatRoomMemberService;

    @Transactional
    public ChatRoomSummary create(ChatRoomCreate command, AuthenticatedUser user) {
        try {
            validateOperators(command.operators(), command.host().operatorId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Set<ChatRoomMember> chatRoomMembers = new HashSet<>();
        // save host
        chatRoomMembers.add(chatRoomMemberService.createOperator(command.host(), Role.HOST, user));
        // save operators
        for (ChatRoomOperatorCreate chatRoomOperatorCreate : command.operators()) {
            chatRoomMembers.add(chatRoomMemberService.createOperator(chatRoomOperatorCreate, Role.OPERATOR, user));
        }

        // TODO: @Ian save open chat in DB

        // TODO: @Ian save NFTs

        // TODO: @Ian kakaoService.createOpenChatRoom

        return null;
    }

    @Transactional(readOnly = true)
    public List<ChatRoomRow> getAllChatRooms() {
        List<ChatRoom> entities = chatRoomRepository.findAll();
        return chatRoomAssembler.toRows(entities);
    }

    private void validateOperators(List<ChatRoomOperatorCreate> operatorsCommand, Long hostOperatorId) throws Exception {
        if (operatorsCommand.size() > OPERATOR_SIZE_LIMIT) {
            throw new ChatRoomExceedOperatorLimitException();
        }
        for (ChatRoomOperatorCreate command : operatorsCommand) {
            if (command.operatorId().equals(hostOperatorId)) {
                throw new OperatorAndHostHaveSameId(command.operatorId());
            }
        }
    }

}
