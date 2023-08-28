package com.klipwallet.membership.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationAssembler;
import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationDto;
import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationDto.Application;
import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationDto.RejectRequest;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.PartnerApplication;
import com.klipwallet.membership.entity.PartnerApplication.Status;
import com.klipwallet.membership.exception.member.PartnerApplicationDuplicatedException;
import com.klipwallet.membership.exception.member.PartnerApplicationNotFoundException;
import com.klipwallet.membership.repository.PartnerApplicationRepository;

import static com.klipwallet.membership.entity.PartnerApplication.Status.APPLIED;
import static com.klipwallet.membership.entity.PartnerApplication.Status.APPROVED;

@Service
@RequiredArgsConstructor
public class PartnerApplicationService {
    private final PartnerApplicationRepository partnerApplicationRepository;

    private final PartnerApplicationAssembler partnerApplicationAssembler;


    private void verifyApply(AuthenticatedUser user) {
        if (partnerApplicationRepository.existsByEmailAndStatusIsIn(user.getEmail(), List.of(APPLIED, APPROVED))) {
            throw new PartnerApplicationDuplicatedException();
        }
    }

    @Transactional
    public PartnerApplicationDto.ApplyResult apply(Application body, AuthenticatedUser user) {
        verifyApply(user);

        PartnerApplication entity = body.toPartnerApplication(user);
        PartnerApplication partnerApplication = partnerApplicationRepository.save(entity);
        return partnerApplicationAssembler.toApplyResult(partnerApplication);
    }


    private PartnerApplication tryGetPartnerApplication(Integer applicationId) {
        return partnerApplicationRepository.findById(applicationId)
                                           .orElseThrow(() -> new PartnerApplicationNotFoundException(applicationId));
    }

    @Transactional(readOnly = true)
    public List<PartnerApplicationDto.PartnerApplicationRow> getPartnerApplications(Pageable page, Status status) {
        // TODO KLDV-3068 get and check partner business number from drops
        // TODO consider adding a cache; some results are from drops

        Pageable pageable = PageRequest.of(page.getPageNumber(), page.getPageSize(), toSort(status));

        Page<PartnerApplication> partnerApplications = partnerApplicationRepository.findAllByStatus(status, pageable);
        return partnerApplicationAssembler.toPartnerApplicationRow(partnerApplications);
    }

    private Sort toSort(Status status) {
        if (status == APPLIED) {
            return Sort.sort(PartnerApplication.class).by(PartnerApplication::getCreatedAt).descending();
        }
        return Sort.sort(PartnerApplication.class).by(PartnerApplication::getProcessedAt).descending();
    }

    @Transactional
    public void approve(Integer applicationId, AuthenticatedUser user) {
        PartnerApplication partnerApplication = tryGetPartnerApplication(applicationId);

        partnerApplication.approve(user.getMemberId());
        partnerApplicationRepository.save(partnerApplication);

    }

    @Transactional
    public void reject(Integer applicationId, RejectRequest body, AuthenticatedUser user) {
        PartnerApplication partnerApplication = tryGetPartnerApplication(applicationId);


        partnerApplication.reject(body.rejectReason(), user.getMemberId());
        partnerApplicationRepository.save(partnerApplication);
    }
}
