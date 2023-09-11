package com.klipwallet.membership.dto.partner.application;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.dto.datetime.DateTimeAssembler;
import com.klipwallet.membership.dto.member.MemberAssembler;
import com.klipwallet.membership.dto.partner.application.PartnerApplicationDto.ApplyResult;
import com.klipwallet.membership.dto.partner.application.PartnerApplicationDto.PartnerApplicationRow;
import com.klipwallet.membership.entity.PartnerApplication;

@Component
@RequiredArgsConstructor
public class PartnerApplicationAssembler {
    private final DateTimeAssembler dateTimeAssembler;
    private final MemberAssembler memberAssembler;

    @NonNull
    public ApplyResult toApplyResult(@NonNull PartnerApplication partnerApplication) {
        return new ApplyResult(partnerApplication.getId(), partnerApplication.getBusinessName(),
                               dateTimeAssembler.toOffsetDateTime(partnerApplication.getCreatedAt()));
    }

    @NonNull
    public List<PartnerApplicationRow> toPartnerApplicationRow(@NonNull Page<PartnerApplication> partnerApplications) {

        return partnerApplications.stream()
                                  .map(p -> new PartnerApplicationRow(p.getId(), p.getBusinessName(), p.getKlipDropsPartnerId(),
                                                                      dateTimeAssembler.toOffsetDateTime(p.getCreatedAt()),
                                                                      dateTimeAssembler.toOffsetDateTime(p.getProcessedAt()),
                                                                      memberAssembler.getMemberSummary(p.getProcessorId())))
                                  .toList();
    }
}