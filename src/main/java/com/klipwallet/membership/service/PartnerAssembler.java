package com.klipwallet.membership.service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.dto.datetime.DateTimeAssembler;
import com.klipwallet.membership.dto.member.PartnerDto.AcceptedPartnerDto;
import com.klipwallet.membership.dto.member.PartnerDto.AppliedPartnerDto;
import com.klipwallet.membership.dto.member.PartnerDto.ApplyResult;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.AppliedPartner;

@Component
@RequiredArgsConstructor
public class PartnerAssembler {
    private final DateTimeAssembler dateTimeAssembler;

    @NonNull
    public Partner toPartner(@NonNull AppliedPartner appliedPartner) {
        return new Partner(appliedPartner.getName(), appliedPartner.getPhoneNumber(), appliedPartner.getBusinessRegistrationNumber(),
                           appliedPartner.getEmail(), appliedPartner.getOAuthId());
    }

    @NonNull
    public ApplyResult toApplyResult(@NonNull AppliedPartner appliedPartner) {
        return new ApplyResult(appliedPartner.getMemberId(), appliedPartner.getName(),
                               dateTimeAssembler.toOffsetDateTime(appliedPartner.getCreatedAt()),
                               dateTimeAssembler.toOffsetDateTime(appliedPartner.getUpdatedAt()));
    }

    @NonNull
    public List<AppliedPartnerDto> toAppliedPartnerDto(@NonNull List<AppliedPartner> appliedPartners) {
        return appliedPartners.stream()
                              .map(p -> new AppliedPartnerDto(p.getMemberId(), p.getName(), dateTimeAssembler.toOffsetDateTime(p.getCreatedAt()),
                                                              p.getStatus(), p.getRejectReason()))
                              .collect(Collectors.toList());
    }

    @NonNull
    public List<AcceptedPartnerDto> toPartnerDto(@NonNull List<Partner> partners) {
        return partners.stream()
                       .map(p -> new AcceptedPartnerDto(p.getMemberId(), p.getName(),
                                                        dateTimeAssembler.toOffsetDateTime(p.getCreatedAt())))
                       .collect(Collectors.toList());
    }
}
