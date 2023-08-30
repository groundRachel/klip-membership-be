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

import com.klipwallet.membership.dto.partner.PartnerAssembler;
import com.klipwallet.membership.dto.partner.PartnerDto.ApprovedPartnerDto;
import com.klipwallet.membership.entity.PartnerApplication.Status;
import com.klipwallet.membership.entity.PartnerSummaryView;
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

    private Sort getSort() {
        return Sort.sort(PartnerSummaryView.class).by(PartnerSummaryView::getProcessedAt).descending();
    }
}
