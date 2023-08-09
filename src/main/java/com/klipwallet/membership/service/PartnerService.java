package com.klipwallet.membership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.member.PartnerDto.Apply;
import com.klipwallet.membership.dto.member.PartnerDto.ApplyResult;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.repository.PartnerRepository;

@Service
@RequiredArgsConstructor
public class PartnerService {
    private final PartnerRepository partnerRepository;

    @Transactional
    public ApplyResult apply(Apply body) {
        Partner entity = body.toPartner();
        Partner partner = partnerRepository.save(entity);
        return new ApplyResult(partner.getId(), partner.getName(), partner.getCreatedAt(), partner.getUpdatedAt());
    }
}
