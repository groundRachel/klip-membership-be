package com.klipwallet.membership.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.klipwallet.membership.dto.partner.PartnerAssembler;
import com.klipwallet.membership.dto.partner.PartnerDto.ApprovedPartnerDto;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.Partner.PartnerSummaryView;
import com.klipwallet.membership.entity.PartnerApplication;
import com.klipwallet.membership.entity.PartnerApplication.Status;
import com.klipwallet.membership.entity.PartnerApplicationApproved;
import com.klipwallet.membership.repository.PartnerRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerService {
    private final PartnerRepository partnerRepository;
    private final PartnerAssembler partnerAssembler;

    @Transactional(readOnly = true)
    public List<ApprovedPartnerDto> getPartners(Pageable page) {
        Pageable pageable = PageRequest.of(page.getPageNumber(), page.getPageSize(), getSort());

        Page<PartnerSummaryView> partners = partnerRepository.findAllPartners(Status.APPROVED, pageable);
        return partnerAssembler.toPartnerDto(partners);
    }

    @TransactionalEventListener(value = PartnerApplicationApproved.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void subscribePartnerApplicationApproved(PartnerApplicationApproved event) {
        PartnerApplication partnerApplication = event.getPartnerApplication();
        MemberId occurrerId = event.getOccurrerId();
        partnerRepository.save(new Partner(partnerApplication.getBusinessName(), partnerApplication.getPhoneNumber(),
                                           partnerApplication.getBusinessRegistrationNumber(), partnerApplication.getEmail(),
                                           partnerApplication.getOAuthId(), occurrerId));
    }

    private Sort getSort() {
        return Sort.sort(PartnerSummaryView.class).by(PartnerSummaryView::getProcessedAt).descending();
    }
}
