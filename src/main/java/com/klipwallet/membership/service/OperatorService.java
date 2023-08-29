package com.klipwallet.membership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.chatroom.OperatorSummary;
import com.klipwallet.membership.dto.operator.OperatorCreate;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Operator;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.exception.member.PartnerNotFoundException;
import com.klipwallet.membership.repository.OperatorRepository;
import com.klipwallet.membership.repository.PartnerRepository;

@Service
@RequiredArgsConstructor
public class OperatorService {
    private final OperatorRepository operatorRepository;
    private final PartnerRepository partnerRepository;

    @Transactional
    public OperatorSummary create(OperatorCreate command, AuthenticatedUser user) {
        Partner partner = getPartner(user.getMemberId());
        Operator entity = command.toOperator(command.klipId(), partner.getId(), user.getMemberId());
        Operator saved = operatorRepository.save(entity);
        return new OperatorSummary(saved);
    }

    public Partner getPartner(MemberId partnerId) {
        return partnerRepository.findById(partnerId.value()).orElseThrow(() -> new PartnerNotFoundException(partnerId));
    }
}
