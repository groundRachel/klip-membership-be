package com.klipwallet.membership.service;

import jakarta.annotation.Nullable;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import com.klipwallet.membership.config.KlipMembershipProperties;
import com.klipwallet.membership.dto.operator.OperatorSummary;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.InviteOperatorNotifiable;
import com.klipwallet.membership.entity.KlipUser;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Operator;
import com.klipwallet.membership.entity.OperatorInvitation;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.kakao.KakaoId;
import com.klipwallet.membership.entity.utils.PhoneNumberUtils;
import com.klipwallet.membership.exception.ForbiddenException;
import com.klipwallet.membership.exception.member.PartnerNotFoundException;
import com.klipwallet.membership.exception.operator.OperationAlreadyJoinedException;
import com.klipwallet.membership.exception.operator.OperatorInvitationCodeExpiredException;
import com.klipwallet.membership.exception.operator.OperatorInvitationExpiredException;
import com.klipwallet.membership.exception.operator.OperatorInvitationNotMatchedException;
import com.klipwallet.membership.exception.operator.OperatorInviteeNotExistsOnKlipException;
import com.klipwallet.membership.exception.operator.OperatorNotFoundException;
import com.klipwallet.membership.repository.OperatorRepository;
import com.klipwallet.membership.repository.PartnerRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperatorService implements OperatorInvitable {
    private final KlipMembershipProperties klipMembershipProperties;
    private final OperatorRepository operatorRepository;
    private final PartnerRepository partnerRepository;
    private final KlipAccountService klipAccountService;
    private final InvitationRegistry invitationRegistry;
    private final InvitationNotifier invitationNotifier;

    /**
     * 운영진 가입
     * <p>
     * 파트너가 초대 후 운영진이 특정 링크를 클릭한 후 발급해 둔 초대 코드를 바탕으로 처리한다.
     * </p>
     *
     * @param invitationCode 초대 코드
     * @param user           운영진 가입을 하고자 하는 카카오 이용자
     * @return 가입된 운영진 요약 정보
     */
    @Transactional
    public OperatorSummary join(@Nullable String invitationCode, @NonNull AuthenticatedUser user) {
        // 초대 코드와 정보 유효성 체크
        OperatorInvitation invitation = verifiedInvitation(invitationCode, user);
        KlipUser klipUser = getKlipUser(user);
        // 운영진으로 초대 받은 사람, 조회된 Klkp 이용자, 인증한 Kakao 이용자가 동일한가?
        verifyInviter(invitation, klipUser, user);
        // 운영진 가입 여부 체크
        checkAlreadyJoined(klipUser);
        // 초대한 파트너
        Partner partner = tryGetPartner(invitation.getInviterPartnerId());
        Operator saved = createOperator(klipUser, partner);
        // 초대 정보 Clear
        clearInvitation(invitationCode);
        return new OperatorSummary(saved);
    }

    private void clearInvitation(@Nullable String invitationCode) {
        invitationRegistry.delete(invitationCode);
    }

    private void checkAlreadyJoined(KlipUser klipUser) {
        if (operatorRepository.existsByKakaoUserId(klipUser.getKakaoUserId())) {
            throw new OperationAlreadyJoinedException();
        }
    }

    private KlipUser getKlipUser(@NonNull AuthenticatedUser user) {
        KakaoId kakaoId = new KakaoId(user.getName());
        return klipAccountService.getKlipUser(kakaoId);
    }

    @NonNull
    private OperatorInvitation verifiedInvitation(@Nullable String invitationCode, @NonNull AuthenticatedUser user) {
        if (!user.isKakao()) {
            throw new ForbiddenException(user);
        }
        // 초대 코드 만료(session timeout: 30m)
        if (invitationCode == null) {
            throw new OperatorInvitationCodeExpiredException();
        }
        OperatorInvitation invitation = invitationRegistry.lookup(invitationCode);
        // 초대 만료 or 미존재(24h)
        if (invitation == null) {
            throw new OperatorInvitationExpiredException();
        }
        return invitation;
    }

    private void verifyInviter(OperatorInvitation invitation, KlipUser klipUser, AuthenticatedUser kakaoUser) {
        String inviterMobileNumber = invitation.getInviteeMobileNumber();
        verifyInviterMobileNumber(inviterMobileNumber, klipUser.getPhone(),
                                  "Not matched inviterMobileNumber: {} and klipPhoneNumber: {}");
        verifyInviterMobileNumber(inviterMobileNumber, kakaoUser.getKakaoPhoneNumber(),
                                  "Not matched inviterMobileNumber: {} and kakaoPhoneNumber: {}");
    }

    private void verifyInviterMobileNumber(String inviterMobileNumber, String targetPhoneNumber, String errorMessage) {
        String formalNumberPhoneNumber = toFormalNumber(targetPhoneNumber);
        if (!inviterMobileNumber.equals(formalNumberPhoneNumber)) {
            log.error(errorMessage, inviterMobileNumber, targetPhoneNumber);
            throw new OperatorInvitationNotMatchedException();
        }
    }

    private String toFormalNumber(String phoneNumber) {
        try {
            return PhoneNumberUtils.toFormalKrMobileNumber(phoneNumber);
        } catch (IllegalArgumentException cause) {
            throw new OperatorInvitationNotMatchedException(cause);
        }
    }

    private Operator createOperator(KlipUser klipUser, Partner partner) {
        @SuppressWarnings("DataFlowIssue")
        Operator entity = new Operator(klipUser.getKlipAccountId(), klipUser.getKakaoUserId(), partner.getMemberId());
        return operatorRepository.save(entity);
    }

    public Operator tryGetOperator(Long operatorId) {
        return operatorRepository.findById(operatorId).orElseThrow(() -> new OperatorNotFoundException(operatorId));
    }

    private Partner tryGetPartner(MemberId partnerId) {
        return partnerRepository.findById(partnerId.value()).orElseThrow(() -> new PartnerNotFoundException(partnerId));
    }

    /**
     * {@inheritDoc}
     *
     * @param inviterPartnerId 초대한 파트너 아이디
     * @param inviteePhoneNumber      초대 받은 운영진의 휴대폰 번호
     * @return 초대 URL
     */
    @Override
    public String inviteOperator(MemberId inviterPartnerId, String phoneNumber) {
        // 초대 만료와 코드 관리를 위해서 필요함.
        String inviteePhoneNumber = PhoneNumberUtils.toFormalKrMobileNumber(phoneNumber);
        KlipUser invitee = klipAccountService.getKlipUserByPhoneNumber(inviteePhoneNumber);
        if (invitee == null) {
            throw new OperatorInviteeNotExistsOnKlipException(inviteePhoneNumber);
        }
        String code = invitationRegistry.save(new OperatorInvitation(inviterPartnerId, inviteePhoneNumber));
        Partner inviterPartner = tryGetPartner(inviterPartnerId);
        return sendNotification(inviterPartner, inviteePhoneNumber, code);
    }

    @NonNull
    private String sendNotification(Partner partner, String inviteePhoneNumber, String code) {
        String invitationUrl = toInvitationUrl(code);

        InviteOperatorNotifiable command = new InviteOperatorNotifiable(inviteePhoneNumber, invitationUrl, partner.getName());
        invitationNotifier.notifyToInviteOperator(command);
        return invitationUrl;
    }

    private String toInvitationUrl(String code) {
        String inviteOperatorUrl = klipMembershipProperties.getInviteOperatorUrl();
        return UriComponentsBuilder.fromHttpUrl(inviteOperatorUrl)
                                   .queryParam("code", code)
                                   .build(true)
                                   .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    @Override
    public boolean isAlreadyOperator(String kakaoUserId) {
        return operatorRepository.existsByKakaoUserId(kakaoUserId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OperatorInvitation lookup(String invitationCode) {
        return invitationRegistry.lookup(invitationCode);
    }
}
