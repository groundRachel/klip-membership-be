package com.klipwallet.membership.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.partner.PartnerAssembler;
import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationDto;
import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationAssembler;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.PartnerApplication;
import com.klipwallet.membership.exception.member.PartnerApplicationAlreadyProcessedException;
import com.klipwallet.membership.exception.member.PartnerApplicationNotFoundException;
import com.klipwallet.membership.repository.PartnerRepository;
import com.klipwallet.membership.repository.PartnerApplicationRepository;

import static com.klipwallet.membership.entity.PartnerApplication.Status.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerApplicationService {
    private final PartnerApplicationRepository partnerApplicationRepository;
    private final PartnerRepository partnerRepository;

    private final PartnerApplicationAssembler partnerApplicationAssembler;
    private final PartnerAssembler partnerAssembler;

    @Transactional
    public PartnerApplicationDto.ApplyResult apply(PartnerApplicationDto.Application body) {
        PartnerApplication entity = body.toPartnerApplication();
        PartnerApplication partnerApplication = partnerApplicationRepository.save(entity);
        return partnerApplicationAssembler.toApplyResult(partnerApplication);
    }

    @Transactional(readOnly = true)
    public List<PartnerApplicationDto.PartnerApplicationRow> getPartnerApplications() {
        // TODO KLDV-3066 Pagination
        // TODO KLDV-3068 get and check partner business number from drops
        // TODO consider adding a cache; some results are from drops
        List<PartnerApplication> partnerApplications = partnerApplicationRepository.findAll();
        return partnerApplicationAssembler.toPartnerApplicationRow(partnerApplications);
    }

    @Transactional
    public void approve(Integer applicationId) {
        PartnerApplication partnerApplication = partnerApplicationRepository.findById(applicationId)
                                                                            .orElseThrow(
                                                                                    () -> new PartnerApplicationNotFoundException(applicationId));

        if (partnerApplication.getStatus() != APPLIED) {
            // TODO Winnie https://github.com/ground-x/klip-membership-be/pull/9#discussion_r1297000998
            throw new PartnerApplicationAlreadyProcessedException(partnerApplication);
        }

        partnerApplication.approve();
        partnerApplicationRepository.save(partnerApplication);

        Partner partner = partnerAssembler.toPartner(partnerApplication);
        partnerRepository.save(partner);

        // TODO KLDV-3069 send result by email
    }

    @Transactional
    public void reject(Integer applicationId, PartnerApplicationDto.RejectRequest body) {
        PartnerApplication partnerApplication = partnerApplicationRepository.findById(applicationId)
                                                                            .orElseThrow(
                                                                                    () -> new PartnerApplicationNotFoundException(applicationId));
        if (partnerApplication.getStatus() != APPLIED) {
            // TODO Winnie https://github.com/ground-x/klip-membership-be/pull/9#discussion_r1297000998
            throw new PartnerApplicationAlreadyProcessedException(partnerApplication);
        }

        partnerApplication.reject(body.rejectReason());
        partnerApplicationRepository.save(partnerApplication);

        // TODO KLDV-3069 send result by email
    }
}
