package com.klipwallet.membership.dto.partner;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.dto.datetime.DateTimeAssembler;
import com.klipwallet.membership.dto.partner.PartnerDto.ApprovedPartnerDto;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.PartnerApplication;

@Component
@RequiredArgsConstructor
public class PartnerAssembler {
    private final DateTimeAssembler dateTimeAssembler;

    @NonNull
    public Partner toPartner(@NonNull PartnerApplication partnerApplication) {
        return new Partner(partnerApplication.getName(), partnerApplication.getPhoneNumber(), partnerApplication.getBusinessRegistrationNumber(),
                           partnerApplication.getEmail(), partnerApplication.getOAuthId());
    }

    @NonNull
    public List<ApprovedPartnerDto> toPartnerDto(@NonNull List<Partner> partners) {
        return partners.stream()
                       .map(p -> new ApprovedPartnerDto(p.getMemberId(), p.getName(),
                                                        dateTimeAssembler.toOffsetDateTime(p.getCreatedAt())))
                       .collect(Collectors.toList());
    }
}
