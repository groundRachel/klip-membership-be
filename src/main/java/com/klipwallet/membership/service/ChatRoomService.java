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
import com.klipwallet.membership.entity.kakao.KakaoId;
import com.klipwallet.membership.entity.kakao.OpenChatRoomHost;
import com.klipwallet.membership.entity.kakao.OpenChatRoomSummary;
import com.klipwallet.membership.exception.kakao.OperatorAlreadyExistsException;
import com.klipwallet.membership.exception.member.OperatorDuplicatedException;
import com.klipwallet.membership.repository.ChatRoomRepository;
import com.klipwallet.membership.repository.OperatorRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final KakaoService kakaoService;
    private final ChatRoomAssembler chatRoomAssembler;
    private final OperatorRepository operatorRepository;
    private final ChatRoomMemberService chatRoomMemberService;

    @Transactional
    public ChatRoomSummary create(ChatRoomCreate command, AuthenticatedUser user) {
        checkOperators(command.operators(), command.host().operatorId());

        Set<ChatRoomMember> chatRoomMembers = new HashSet<>();
        // save host
        ChatRoomMember savedHost = chatRoomMemberService.createOperator(command.host(), Role.HOST, user);
        chatRoomMembers.add(savedHost);
        // save operators
        for (ChatRoomOperatorCreate chatRoomOperatorCreate : command.operators()) {
            chatRoomMembers.add(chatRoomMemberService.createOperator(chatRoomOperatorCreate, Role.OPERATOR, user));
        }

        OpenChatRoomSummary summary = kakaoService.createOpenChatRoom(command.title(), command.description(), command.coverImageUrl(),
                                                                      new OpenChatRoomHost(new KakaoId(savedHost.getKakaoUserId()),
                                                                                           command.host().nickname(),
                                                                                           command.host().profileImageUrl()));
        log.info(summary.toString());
        // TODO: @Ian save open chat in DB

        // TODO: @Ian save NFTs

        return null;
    }

    @Transactional(readOnly = true)
    public List<ChatRoomRow> getAllChatRooms() {
        List<ChatRoom> entities = chatRoomRepository.findAll();
        return chatRoomAssembler.toRows(entities);
    }

    private void checkOperators(List<ChatRoomOperatorCreate> operatorsCommand, Long hostOperatorId) {
        Set<Long> operatorIds = new HashSet<>();
        for (ChatRoomOperatorCreate command : operatorsCommand) {
            if (command.operatorId().equals(hostOperatorId)) {
                throw new OperatorAlreadyExistsException(command.operatorId());
            }
            if (operatorIds.contains(command.operatorId())) {
                throw new OperatorDuplicatedException(command.operatorId());
            } else {
                operatorIds.add(command.operatorId());
            }
        }
    }

}
