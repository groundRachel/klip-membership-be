package com.klipwallet.membership.dto.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.Member;
import com.klipwallet.membership.exception.MemberNotFoundException;
import com.klipwallet.membership.repository.MemberRepository;

/**
 * 인증 관련 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberAuthentication getMemberAuthentication(AuthenticatedUser user) {
        if (user == null) {
            return MemberAuthentication.NO_AUTH;
        }
        if (user.isKakao()) {
            return MemberAuthentication.KAKAO;
        }
        if (user.isGoogle()) {
            return MemberAuthentication.GOOGLE;
        }
        if (user.isPartner() || user.isAdmin()) {
            Member member = tryGetMember(user);
            return new MemberAuthentication(member, user);
        }
        log.warn("Invalid user when getMemberAuthentication: {}", user);
        return MemberAuthentication.NO_AUTH;
    }

    @SuppressWarnings("DataFlowIssue")
    private Member tryGetMember(AuthenticatedUser user) {
        return memberRepository.findById(user.getMemberId().value())
                               .filter(Member::isEnabled)
                               .orElseThrow(MemberNotFoundException::new);
    }
}
