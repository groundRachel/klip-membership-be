package com.klipwallet.membership.service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.member.PartnerDto;
import com.klipwallet.membership.entity.AcceptedPartner;
import com.klipwallet.membership.entity.AppliedPartner;
import com.klipwallet.membership.repository.AcceptedPartnerRepository;
import com.klipwallet.membership.repository.AppliedPartnerRepository;

@Service
@RequiredArgsConstructor
public class PartnerService {
    private final AppliedPartnerRepository appliedPartnerRepository;
    private final AcceptedPartnerRepository acceptedPartnerRepository;

    @Transactional
    public PartnerDto.ApplyResult apply(PartnerDto.Apply body) {
        AppliedPartner entity = body.toAppliedPartner();
        AppliedPartner appliedPartner = appliedPartnerRepository.save(entity);
        return new PartnerDto.ApplyResult(appliedPartner.getId(), appliedPartner.getName(), appliedPartner.getCreatedAt(),
                                          appliedPartner.getUpdatedAt());
    }
}
