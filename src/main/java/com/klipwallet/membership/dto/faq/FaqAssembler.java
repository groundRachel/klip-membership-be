package com.klipwallet.membership.dto.faq;

import java.util.Map;

import jakarta.annotation.Nonnull;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.dto.datetime.DateTimeAssembler;
import com.klipwallet.membership.dto.member.MemberSummary;
import com.klipwallet.membership.entity.BaseMeta;
import com.klipwallet.membership.entity.Faq;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.service.MemberAssembler;

@Component
@RequiredArgsConstructor
public class FaqAssembler {
    private final DateTimeAssembler dateTimeAssembler;
    private final MemberAssembler memberAssembler;
    @Nonnull
    public FaqDetail toDetail(@NonNull Faq faq) {
        BaseMeta base = faq.getBase();
        Map<MemberId, MemberSummary> members = memberAssembler.getMemberSummaryMap(base.getCreatedBy(), base.getUpdatedBy());
        return new FaqDetail(faq.getId(), faq.getTitle(), faq.getBody(), faq.getStatus(),
                             dateTimeAssembler.toOffsetDateTime(base.getCreatedAt()),
                             dateTimeAssembler.toOffsetDateTime(base.getUpdatedAt()),
                             members.getOrDefault(base.getCreatedBy(), MemberSummary.deactivated(base.getCreatedBy())),
                             members.getOrDefault(base.getUpdatedBy(), MemberSummary.deactivated(base.getUpdatedBy())));
    }
}
