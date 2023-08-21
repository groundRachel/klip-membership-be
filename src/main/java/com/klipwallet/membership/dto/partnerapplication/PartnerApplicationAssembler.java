package com.klipwallet.membership.dto.partnerapplication;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.dto.datetime.DateTimeAssembler;
import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationDto.ApplyResult;
import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationDto.PartnerApplicationRow;
import com.klipwallet.membership.entity.PartnerApplication;

@Component
@RequiredArgsConstructor
public class PartnerApplicationAssembler {
    private final DateTimeAssembler dateTimeAssembler;

    @NonNull
    public ApplyResult toApplyResult(@NonNull PartnerApplication partnerApplication) {
        return new ApplyResult(partnerApplication.getId(), partnerApplication.getBusinessName(),
                               dateTimeAssembler.toOffsetDateTime(partnerApplication.getCreatedAt()),
                               dateTimeAssembler.toOffsetDateTime(partnerApplication.getUpdatedAt()));
    }

    @NonNull
    public List<PartnerApplicationRow> toPartnerApplicationRow(@NonNull List<PartnerApplication> partnerApplications) {
        return partnerApplications.stream()
                                  .map(p -> new PartnerApplicationRow(p.getId(), p.getBusinessName(),
                                                                      dateTimeAssembler.toOffsetDateTime(p.getCreatedAt()),
                                                                      p.getStatus(), p.getRejectReason()))
                                  .collect(Collectors.toList());
    }
}
