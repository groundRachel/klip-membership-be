package com.klipwallet.membership.dto.partner.application;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.dto.datetime.DateTimeAssembler;
import com.klipwallet.membership.dto.klipdrops.KlipDropsDto.PartnerDetail;
import com.klipwallet.membership.dto.member.MemberAssembler;
import com.klipwallet.membership.dto.partner.application.PartnerApplicationDto.ApplyResult;
import com.klipwallet.membership.dto.partner.application.PartnerApplicationDto.PartnerApplicationDetail;
import com.klipwallet.membership.dto.partner.application.PartnerApplicationDto.PartnerApplicationRow;
import com.klipwallet.membership.dto.partner.application.PartnerApplicationDto.RejectDetail;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.PartnerApplication;
import com.klipwallet.membership.entity.PartnerApplication.Status;

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
                                                                      memberAssembler.getMemberSummaryIfExist((p.getProcessorId()))))
                                  .toList();
    }

    public PartnerApplicationDetail toPartnerApplicationDetail(PartnerApplication partnerApplication) {
        PartnerApplicationDetail detail = new PartnerApplicationDetail(partnerApplication.getId(),
                                                                       partnerApplication.getBusinessName(),
                                                                       partnerApplication.getBusinessRegistrationNumber(),
                                                                       partnerApplication.getStatus(),
                                                                       partnerApplication.getEmail(),
                                                                       dateTimeAssembler.toOffsetDateTime(partnerApplication.getCreatedAt()),
                                                                       new PartnerDetail(partnerApplication.getKlipDropsPartnerId(),
                                                                                         partnerApplication.getKlipDropsPartnerName()),
                                                                       null);
        if (partnerApplication.getStatus() == Status.REJECTED) {
            detail = detail.withRejectDetail(new RejectDetail(dateTimeAssembler.toOffsetDateTime(partnerApplication.getProcessedAt()),
                                                              memberAssembler.getMemberSummary(new MemberId(partnerApplication.getProcessorId())),
                                                              partnerApplication.getRejectReason()));
        }
        return detail;
    }
}
