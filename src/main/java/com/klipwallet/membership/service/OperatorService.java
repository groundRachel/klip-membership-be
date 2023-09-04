package com.klipwallet.membership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.operator.OperatorCreate;
import com.klipwallet.membership.dto.operator.OperatorSummary;
import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.KlipUser;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Operator;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.exception.OperatorNotFoundException;
import com.klipwallet.membership.exception.member.PartnerNotFoundException;
import com.klipwallet.membership.repository.OperatorRepository;
import com.klipwallet.membership.repository.PartnerRepository;

@Service
@RequiredArgsConstructor
public class OperatorService {
    private final OperatorRepository operatorRepository;
    private final PartnerRepository partnerRepository;
    private final KlipAccountService klipAccountService;

    @Transactional
    public OperatorSummary create(OperatorCreate command, AuthenticatedUser user) {
        KlipUser klipUser = tryGetKlipUser(command.klipRequestKey());

        /**
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


}
