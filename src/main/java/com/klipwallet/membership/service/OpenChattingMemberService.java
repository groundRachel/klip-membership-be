package com.klipwallet.membership.service;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.klipwallet.membership.dto.openchatting.OpenChattingMemberCreate;
import com.klipwallet.membership.dto.openchatting.OpenChattingOperatorCreate;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.KlipUser;
import com.klipwallet.membership.entity.OpenChatting;
import com.klipwallet.membership.entity.OpenChattingMember;
import com.klipwallet.membership.entity.OpenChattingMember.Role;
import com.klipwallet.membership.entity.Operator;
import com.klipwallet.membership.exception.ForbiddenException;
import com.klipwallet.membership.exception.InvalidRequestException;
import com.klipwallet.membership.exception.MemberNotFoundException;
import com.klipwallet.membership.exception.kakao.HostOpenChattingLimitExceeded;
import com.klipwallet.membership.exception.kakao.KakaoForbiddenInternalApiException;
import com.klipwallet.membership.repository.OpenChattingMemberRepository;
import com.klipwallet.membership.service.kakao.KakaoService;

import static com.klipwallet.membership.exception.ErrorCode.OPEN_CHATTING_ACCESS_DENIED;

@Service
@RequiredArgsConstructor
public class OpenChattingMemberService {
    private static final Long HOST_OPEN_CHATTING_LIMIT = 10L;
    private final OpenChattingMemberRepository openChattingMemberRepository;
    private final OperatorService operatorService;
    private final KakaoService kakaoService;

    public OpenChattingMember createMember(OpenChatting openChatting, OpenChattingMemberCreate command, KlipUser klipUser) {
        OpenChattingMember member;
        try {
            member = getOpenChattingMemberByOpenChattingIdAndKlipId(openChatting.getId(), klipUser.getKlipAccountId());
        } catch (MemberNotFoundException e) {
            // 최초로 채팅방에 참여하는 경우 DB 저장하기
            if (!StringUtils.hasText(command.nickname()) || !StringUtils.hasText(command.profileImageUrl())) {
                throw new InvalidRequestException("nickname, profile required");
            }
            OpenChattingMember entity = command.toOpenChattingMember(openChatting.getId(), klipUser.getKlipAccountId(), klipUser.getKakaoUserId());
            member = openChattingMemberRepository.save(entity);
        }

        // 오픈채팅 참여하기
        try {
            kakaoService.joinOpenChatting(openChatting, member);
        } catch (KakaoForbiddenInternalApiException e) {
            throw new ForbiddenException(OPEN_CHATTING_ACCESS_DENIED);
        }
        return member;
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
