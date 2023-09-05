package com.klipwallet.membership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import com.klipwallet.membership.config.KlipMembershipProperties;
import com.klipwallet.membership.dto.operator.OperatorCreate;
import com.klipwallet.membership.dto.operator.OperatorSummary;
import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.KlipUser;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Operator;
import com.klipwallet.membership.entity.OperatorInvitation;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.exception.OperatorNotFoundException;
import com.klipwallet.membership.exception.member.PartnerNotFoundException;
import com.klipwallet.membership.repository.OperatorRepository;
import com.klipwallet.membership.repository.PartnerRepository;
import com.klipwallet.membership.service.kakao.KakaoBizMessageService;

@Service
@RequiredArgsConstructor
public class OperatorService implements OperatorInvitable {
    private final KlipMembershipProperties klipMembershipProperties;
    private final OperatorRepository operatorRepository;
    private final PartnerRepository partnerRepository;
    private final KlipAccountService klipAccountService;
    private final InvitationRegistry invitationRegistry;
    private final KakaoBizMessageService kakaoBizMessageService;

    @Transactional
    public OperatorSummary create(OperatorCreate command, AuthenticatedUser user) {
        KlipUser klipUser = tryGetKlipUser(command.klipRequestKey());

        /*
         * TODO: @Ian 가져온 클립 유저 정보 KlipAccount 테이블에 저장
         * {@link com.klipwallet.membership.adaptor.klip.KlipAccount}
         */

        Partner partner = tryGetPartner(user.getMemberId());

        Operator entity = command.toOperator(klipUser.getKlipAccountId(), klipUser.getKakaoUserId(), partner.getId(), user.getMemberId());
        Operator saved = operatorRepository.save(entity);
        return new OperatorSummary(saved);
    }

    public Operator tryGetOperator(Long operatorId) {
        return operatorRepository.findById(operatorId).orElseThrow(() -> new OperatorNotFoundException(operatorId));
    }

    private Partner tryGetPartner(MemberId partnerId) {
        return partnerRepository.findById(partnerId.value()).orElseThrow(() -> new PartnerNotFoundException(partnerId));
    }

    private KlipUser tryGetKlipUser(String requestKey) {
        // TODO: @Ian get klaytn address by a2a adaptor
        return klipAccountService.getKlipUser(new Address(""));
    }

    /**
     * {@inheritDoc}
     *
     * @param partnerId
     * @param phoneNumber 휴대폰 번호
     * @return 초대 URL
     */
    @Override
    public String inviteOperator(MemberId partnerId, String phoneNumber) {
        // 초대 만료와 코드 관리를 위해서 필요함.
        String code = invitationRegistry.save(new OperatorInvitation(partnerId, phoneNumber));
        String invitationUrl = toInvitationUrl(code);
        // TODO @Jordan 카카오 알림톡 발송 on Async
        kakaoBizMessageService.sendNotificationTalk(phoneNumber, null);
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
