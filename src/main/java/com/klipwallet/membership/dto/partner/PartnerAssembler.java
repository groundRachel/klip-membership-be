package com.klipwallet.membership.dto.partner;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.dto.datetime.DateTimeAssembler;
import com.klipwallet.membership.dto.member.MemberAssembler;
import com.klipwallet.membership.dto.member.MemberSummary;
import com.klipwallet.membership.dto.partner.PartnerDto.ApproveDetail;
import com.klipwallet.membership.dto.partner.PartnerDto.ApprovedPartnerDto;
import com.klipwallet.membership.dto.partner.PartnerDto.DetailByAdmin;
import com.klipwallet.membership.dto.partner.PartnerDto.DetailByTool;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.PartnerDetailView;
import com.klipwallet.membership.entity.PartnerSummaryView;

@Component
@RequiredArgsConstructor
public class PartnerAssembler {
    private final DateTimeAssembler dateTimeAssembler;
    private final MemberAssembler memberAssembler;

    public List<ApprovedPartnerDto> toPartnerDto(Page<PartnerSummaryView> partners) {
        return partners.stream().map(p -> {
                           MemberSummary processor = null;
                           if (p.getProcessorId() != null) {
                               processor = memberAssembler.getMemberSummary(new MemberId(p.getProcessorId()));
                           }
                           return new ApprovedPartnerDto(new MemberId(p.getMemberId()), p.getName(),
                                                         dateTimeAssembler.toOffsetDateTime(p.getProcessedAt()),
                                                         processor);
                       })
                       .toList();

    }

    public DetailByTool toDetailByTool(Partner partner) {
        return new DetailByTool(partner.getName(), partner.getBusinessRegistrationNumber(), partner.getPhoneNumber());
    }

    public DetailByAdmin toDetailByAdmin(PartnerDetailView partner) {
        return new DetailByAdmin(new MemberId(partner.getId()),
                                 partner.getName(),
                                 partner.getBusinessRegistrationNumber(),
                                 partner.getEmail(),
                                 dateTimeAssembler.toOffsetDateTime(partner.getCreatedAt()),
                                 partner.getKlipDropsPartnerId(),
                                 new ApproveDetail(memberAssembler.getMemberSummary(partner.getProcessorId()),
                                                   dateTimeAssembler.toOffsetDateTime(partner.getProcessedAt())));
    }
}
