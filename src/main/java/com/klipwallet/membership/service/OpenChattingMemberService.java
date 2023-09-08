package com.klipwallet.membership.service;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.klipwallet.membership.dto.openchatting.OpenChattingMemberCreate;
import com.klipwallet.membership.dto.openchatting.OpenChattingMemberSummary;
import com.klipwallet.membership.dto.openchatting.OpenChattingOperatorCreate;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.OpenChatting;
import com.klipwallet.membership.entity.OpenChattingMember;
import com.klipwallet.membership.entity.OpenChattingMember.Role;
import com.klipwallet.membership.entity.Operator;
import com.klipwallet.membership.exception.MemberNotFoundException;
import com.klipwallet.membership.exception.kakao.HostOpenChattingLimitExceeded;
import com.klipwallet.membership.repository.OpenChattingMemberRepository;
import com.klipwallet.membership.service.kakao.KakaoService;

@Service
@RequiredArgsConstructor
public class OpenChattingMemberService {
    private static final Long HOST_OPEN_CHATTING_LIMIT = 10L;
    private final OpenChattingMemberRepository openChattingMemberRepository;
    private final OperatorService operatorService;
    private final KakaoService kakaoService;


    /**
     * 외부 API 에서 사용
     */
    public OpenChattingMemberSummary create(OpenChattingMemberCreate command) {
        if (command.role() == Role.NFT_HOLDER) {
            // TODO: Check NFT
        }
        return null;
    }

    public OpenChattingMember createHost(OpenChatting openChatting, OpenChattingOperatorCreate command, AuthenticatedUser user) {
        checkNumOfHostOpenChatting(command.operatorId());
        Operator operator = operatorService.tryGetOperator(command.operatorId());
        // Check operator in partner
        operator.checkPartnerId(user.getMemberId());

        OpenChattingMember entity = command.toOpenChattingMember(openChatting, operator, Role.HOST);
        return openChattingMemberRepository.save(entity);
    }

    public List<OpenChattingMember> createOperators(OpenChatting openChatting, List<OpenChattingOperatorCreate> commands, AuthenticatedUser user) {
        List<OpenChattingMember> openChattingMembers = new ArrayList<>();
        for (OpenChattingOperatorCreate openChattingOperatorCreate : commands) {
            Operator operator = operatorService.tryGetOperator(openChattingOperatorCreate.operatorId());

            OpenChattingMember openChattingMember = openChattingOperatorCreate.toOpenChattingMember(openChatting, operator, Role.OPERATOR);
            openChattingMembers.add(openChattingMember);

            kakaoService.joinOpenChatting(openChatting, openChattingMember);
        }
       return openChattingMemberRepository.saveAll(openChattingMembers);
    }


    private void checkNumOfHostOpenChatting(Long hostId) {
        Long count = openChattingMemberRepository.countByOperatorIdAndRole(hostId, Role.HOST);
        if (count > HOST_OPEN_CHATTING_LIMIT) {
            throw new HostOpenChattingLimitExceeded(hostId, count);
        }
    }

    public OpenChattingMember getOpenChattingMemberByOpenChattingIdAndKlipId(Long openChattingId, Long klipId) {
        return openChattingMemberRepository.findByOpenChattingIdAndKlipId(openChattingId, klipId).orElseThrow(() -> new MemberNotFoundException());
    }
}
