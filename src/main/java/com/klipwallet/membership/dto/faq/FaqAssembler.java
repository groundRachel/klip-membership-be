package com.klipwallet.membership.dto.faq;

import java.util.Map;

import jakarta.annotation.Nonnull;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.dto.datetime.DateTimeAssembler;
import com.klipwallet.membership.dto.member.MemberSummary;
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
        Map<MemberId, MemberSummary> members = memberAssembler.getMemberSummaryMapBy(faq);
        return new FaqDetail(faq.getId(), faq.getTitle(), faq.getBody(), faq.getStatus(),
                             dateTimeAssembler.toOffsetDateTime(faq.getLivedAt()),
                             dateTimeAssembler.toOffsetDateTime(faq.getCreatedAt()),
                             dateTimeAssembler.toOffsetDateTime(faq.getUpdatedAt()),
                             members.getOrDefault(faq.getCreatorId(), MemberSummary.deactivated(faq.getCreatorId())),
                             members.getOrDefault(faq.getUpdaterId(), MemberSummary.deactivated(faq.getUpdaterId())));
    }
}
