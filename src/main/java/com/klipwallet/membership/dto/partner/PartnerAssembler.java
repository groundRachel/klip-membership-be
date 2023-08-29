package com.klipwallet.membership.dto.partner;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.dto.datetime.DateTimeAssembler;
import com.klipwallet.membership.dto.member.MemberAssembler;
import com.klipwallet.membership.dto.partner.PartnerDto.ApprovedPartnerDto;
import com.klipwallet.membership.entity.Partner.PartnerSummaryView;

@Component
@RequiredArgsConstructor
public class PartnerAssembler {
    private final DateTimeAssembler dateTimeAssembler;
    private final MemberAssembler memberAssembler;

    public List<ApprovedPartnerDto> toPartnerDto(Page<PartnerSummaryView> partners) {
        return partners.stream().map(p -> new ApprovedPartnerDto(p.getMemberId(), p.getName(), dateTimeAssembler.toOffsetDateTime(p.getProcessedAt()),
                                                                 memberAssembler.getMemberSummary(p.getProcessorId()))).toList();

    }
}
