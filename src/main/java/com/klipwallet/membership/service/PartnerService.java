package com.klipwallet.membership.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.member.PartnerDto;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.AppliedPartner;
import com.klipwallet.membership.exception.member.PartnerApplicationAlreadyProcessedException;
import com.klipwallet.membership.exception.member.PartnerApplicationNotFoundException;
import com.klipwallet.membership.repository.PartnerRepository;
import com.klipwallet.membership.repository.AppliedPartnerRepository;

import static com.klipwallet.membership.entity.AppliedPartner.Status.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerService {
    private final AppliedPartnerRepository appliedPartnerRepository;
    private final PartnerRepository partnerRepository;

    private final PartnerAssembler partnerAssembler;

    @Transactional
    public PartnerDto.ApplyResult apply(PartnerDto.Application body) {
        AppliedPartner entity = body.toAppliedPartner();
        AppliedPartner appliedPartner = appliedPartnerRepository.save(entity);
        return partnerAssembler.toApplyResult(appliedPartner);
    }

    @Transactional(readOnly = true)
    public List<PartnerDto.AppliedPartnerDto> getAppliedPartners() {
        // TODO KLDV-3066 Pagination
        // TODO KLDV-3068 get and check partner business number from drops
        // TODO consider adding a cache; some results are from drops
        List<AppliedPartner> appliedPartners = appliedPartnerRepository.findAll();
        return partnerAssembler.toAppliedPartnerDto(appliedPartners);
    }

    @Transactional(readOnly = true)
    public List<PartnerDto.AcceptedPartnerDto> getApprovedPartners() {
        // TODO KLDV-3070 Pagination
        List<Partner> partners = partnerRepository.findAll();
        return partnerAssembler.toPartnerDto(partners);
    }

    @Transactional
    public void approve(PartnerDto.ApproveRequest body) {
        AppliedPartner appliedPartner = appliedPartnerRepository.findById(body.id().value())
                                                                .orElseThrow(() -> new PartnerApplicationNotFoundException(body.id()));

        if (appliedPartner.getStatus() != APPLIED) {
            // TODO Winnie https://github.com/ground-x/klip-membership-be/pull/9#discussion_r1297000998
            throw new PartnerApplicationAlreadyProcessedException(appliedPartner);
        }

        appliedPartner.setApprovedStatus();
        appliedPartnerRepository.save(appliedPartner);

        Partner partner = partnerAssembler.toPartner(appliedPartner);
        partnerRepository.save(partner);

        // TODO KLDV-3069 send result by email
    }

    @Transactional
    public void reject(PartnerDto.RejectRequest body) {
        AppliedPartner appliedPartner = appliedPartnerRepository.findById(body.id().value())
                                                                .orElseThrow(() -> new PartnerApplicationNotFoundException(body.id()));
        if (appliedPartner.getStatus() != APPLIED) {
            // TODO Winnie https://github.com/ground-x/klip-membership-be/pull/9#discussion_r1297000998
            throw new PartnerApplicationAlreadyProcessedException(appliedPartner);
        }

        appliedPartner.setRejectStatus(body.rejectReason());
        appliedPartnerRepository.save(appliedPartner);

        // TODO KLDV-3069 send result by email
    }
}
