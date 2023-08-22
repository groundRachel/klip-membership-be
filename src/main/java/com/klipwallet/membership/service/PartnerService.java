package com.klipwallet.membership.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.partner.PartnerAssembler;
import com.klipwallet.membership.dto.partner.PartnerDto;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.repository.PartnerRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerService {
    private final PartnerRepository partnerRepository;
    private final PartnerAssembler partnerAssembler;

    @Transactional(readOnly = true)
    public List<PartnerDto.ApprovedPartnerDto> getPartners() {
        // TODO KLDV-3070 Pagination
        List<Partner> partners = partnerRepository.findAll();
        return partnerAssembler.toPartnerDto(partners);
    }
}
