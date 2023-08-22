package com.klipwallet.membership.service;

import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.partner.PartnerAssembler;
import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationAssembler;
import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationDto;
import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationDto.Application;
import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationDto.RejectRequest;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.PartnerApplication;
import com.klipwallet.membership.entity.PartnerApplication.Status;
import com.klipwallet.membership.exception.member.PartnerApplicationAlreadyProcessedException;
import com.klipwallet.membership.exception.member.PartnerApplicationDuplicatedException;
import com.klipwallet.membership.exception.member.PartnerApplicationNotFoundException;
import com.klipwallet.membership.repository.PartnerApplicationRepository;
import com.klipwallet.membership.repository.PartnerRepository;

import static com.klipwallet.membership.entity.PartnerApplication.Status.*;

@Service
@RequiredArgsConstructor
public class PartnerApplicationService {
    private final PartnerApplicationRepository partnerApplicationRepository;
    private final PartnerRepository partnerRepository;

    private final PartnerApplicationAssembler partnerApplicationAssembler;
    private final PartnerAssembler partnerAssembler;


    private void canApply(AuthenticatedUser user) {
        partnerApplicationRepository.findByEmailAndStatusIsIn(user.getEmail(), Arrays.asList(APPLIED, APPROVED))
                                    .ifPresent(s -> {
                                        throw new PartnerApplicationDuplicatedException(s);
                                    });
    }

    @Transactional
    public PartnerApplicationDto.ApplyResult apply(Application body, AuthenticatedUser user) {
        canApply(user);

        PartnerApplication entity = body.toPartnerApplication(user);
        PartnerApplication partnerApplication = partnerApplicationRepository.save(entity);
        return partnerApplicationAssembler.toApplyResult(partnerApplication);
    }


    private PartnerApplication tryGetPartnerApplication(Integer applicationId) {
        return partnerApplicationRepository.findById(applicationId)
                                           .orElseThrow(() -> new PartnerApplicationNotFoundException(applicationId));
    }

    private boolean ifCanSkipRequest(Status currentStatus, Status expectedStatus,
                                     MemberId currentUpdatedId, MemberId expectedUpdatedId) {
        // TODO WINNIE testcode
        return currentStatus == expectedStatus && currentUpdatedId == expectedUpdatedId;
    }


    private void checkAppliedStatus(PartnerApplication partnerApplication) {
        if (partnerApplication.getStatus() != APPLIED) {
            throw new PartnerApplicationAlreadyProcessedException(partnerApplication);
        }
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
    public void approve(Integer applicationId, AuthenticatedUser user) {
        PartnerApplication partnerApplication = tryGetPartnerApplication(applicationId);
        if (ifCanSkipRequest(partnerApplication.getStatus(), APPROVED,
                             partnerApplication.getProcessorId(), user.getMemberId())) {
            return;
        }
        checkAppliedStatus(partnerApplication);

        partnerApplication.approve(user.getMemberId());
        partnerApplicationRepository.save(partnerApplication);

        partnerRepository.save(new Partner(partnerApplication.getBusinessName(), partnerApplication.getPhoneNumber(),
                                           partnerApplication.getBusinessRegistrationNumber(), partnerApplication.getEmail(),
                                           partnerApplication.getOAuthId(), user.getMemberId()));

        // TODO KLDV-3069 send result by email
    }

    @Transactional
    public void reject(Integer applicationId, RejectRequest body, AuthenticatedUser user) {
        PartnerApplication partnerApplication = tryGetPartnerApplication(applicationId);
        if (ifCanSkipRequest(partnerApplication.getStatus(), REJECTED,
                             partnerApplication.getProcessorId(), user.getMemberId())) {
            return;
        }
        checkAppliedStatus(partnerApplication);

        partnerApplication.reject(body.rejectReason(), user.getMemberId());
        partnerApplicationRepository.save(partnerApplication);

        // TODO KLDV-3069 send result by email
    }
}
