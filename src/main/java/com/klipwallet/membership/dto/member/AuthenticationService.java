package com.klipwallet.membership.dto.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.entity.Admin;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.SignUpStatus;
import com.klipwallet.membership.exception.member.AdminNotFoundException;
import com.klipwallet.membership.exception.member.PartnerNotFoundException;
import com.klipwallet.membership.repository.AdminRepository;
import com.klipwallet.membership.repository.PartnerRepository;
import com.klipwallet.membership.service.PartnerApplicationGettable;

/**
 * 인증 관련 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final AdminRepository adminRepository;
    private final PartnerRepository partnerRepository;
    private final PartnerApplicationGettable partnerApplicationGetter;

    @Transactional(readOnly = true)
    public MemberAuthentication getMemberAuthentication(AuthenticatedUser user) {
        if (user == null) {
            return MemberAuthentication.NO_AUTH;
        }
        if (user.isPartner()) {
            Partner partner = tryGetPartner(user);
            SignUpStatus signUpStatus = partnerApplicationGetter.getSignUpStatus(user);
            return MemberAuthentication.partner(partner, signUpStatus, user);
        }
        if (user.isGoogle()) {
            SignUpStatus signUpStatus = partnerApplicationGetter.getSignUpStatus(user);
            return MemberAuthentication.google(signUpStatus, user);
        }
        if (user.isAdmin()) {
            Admin admin = tryGetAdmin(user);
            return MemberAuthentication.admin(admin, user);
        }
        if (user.isKakao()) {
            return MemberAuthentication.KAKAO;
        }
        log.warn("Invalid user when getMemberAuthentication: {}", user);
        return MemberAuthentication.NO_AUTH;
    }

    @SuppressWarnings("DataFlowIssue")
    private Partner tryGetPartner(AuthenticatedUser user) {
        return partnerRepository.findById(user.getMemberId().value())
                                .filter(Partner::isEnabled)
                                .orElseThrow(() -> new PartnerNotFoundException(user.getMemberId()));
    }

    @SuppressWarnings("DataFlowIssue")
    private Admin tryGetAdmin(AuthenticatedUser user) {
        return adminRepository.findById(user.getMemberId().value())
                              .filter(Admin::isEnabled)
                              .orElseThrow(() -> new AdminNotFoundException(user.getMemberId()));
    }
}
