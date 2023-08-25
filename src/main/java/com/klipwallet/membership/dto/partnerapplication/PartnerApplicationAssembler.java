package com.klipwallet.membership.dto.partnerapplication;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
                               dateTimeAssembler.toOffsetDateTime(partnerApplication.getCreatedAt()));
    }

    @NonNull
    public List<PartnerApplicationRow> toPartnerApplicationRow(@NonNull Page<PartnerApplication> partnerApplications) {
        return partnerApplications.stream()
                                  .map(p -> new PartnerApplicationRow(p.getId(), p.getBusinessName(),
                                                                      -1,  // TODO fetch info from drops
                                                                      dateTimeAssembler.toOffsetDateTime(p.getCreatedAt()),
                                                                      dateTimeAssembler.toOffsetDateTime(p.getProcessedAt()),
                                                                      // TODO change to admin nickname (current : applicant's nickname)
                                                                      p.getEmail().split("@")[0]))
                                  .collect(Collectors.toList());
    }
}
