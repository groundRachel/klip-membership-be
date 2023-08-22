package com.klipwallet.membership.dto.faq;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Nonnull;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.dto.datetime.DateTimeAssembler;
import com.klipwallet.membership.dto.member.MemberSummary;
import com.klipwallet.membership.entity.Faq;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.service.MemberAssembler;

import static java.util.stream.Collectors.toUnmodifiableSet;

@Component
@RequiredArgsConstructor
public class FaqAssembler {
    private final DateTimeAssembler dateTimeAssembler;
    private final MemberAssembler memberAssembler;
    @Nonnull
    public FaqDetail toDetail(@NonNull Faq faq) {
        Map<MemberId, MemberSummary> members = memberAssembler.getMemberSummaryMap(faq.getAccessorIds());
        return new FaqDetail(faq.getId(), faq.getTitle(), faq.getBody(), faq.getStatus(),
                             dateTimeAssembler.toOffsetDateTime(faq.getLivedAt()),
                             dateTimeAssembler.toOffsetDateTime(faq.getCreatedAt()),
                             dateTimeAssembler.toOffsetDateTime(faq.getUpdatedAt()),
                             members.getOrDefault(faq.getCreatorId(), MemberSummary.deactivated(faq.getCreatorId())),
                             members.getOrDefault(faq.getUpdaterId(), MemberSummary.deactivated(faq.getUpdaterId())));
    }

    public List<FaqRow> toRows(List<Faq> faqs) {
        Map<MemberId, MemberSummary> members = getMemberSummaryMap(faqs);
        return faqs.stream()
                      .map(n -> toRow(n, members))
                      .collect(Collectors.toList());
    }

    private Map<MemberId, MemberSummary> getMemberSummaryMap(List<Faq> faqs) {
        Set<MemberId> memberIds = toAccessorIds(faqs);
        return memberAssembler.getMemberSummaryMap(memberIds);
    }

    private Set<MemberId> toAccessorIds(Collection<Faq> faqs) {
        return faqs.stream()
                      .map(Faq::getAccessorIds)
                      .flatMap(Set::stream)
                      .collect(toUnmodifiableSet());
    }

    private FaqRow toRow(Faq entity, Map<MemberId, MemberSummary> members) {
        MemberId creatorId = entity.getCreatorId();
        MemberId updaterId = entity.getUpdaterId();
        return new FaqRow(entity.getId(), entity.getTitle(), entity.getStatus(),
                       dateTimeAssembler.toOffsetDateTime(entity.getLivedAt()),
                       dateTimeAssembler.toOffsetDateTime(entity.getCreatedAt()),
                       dateTimeAssembler.toOffsetDateTime(entity.getUpdatedAt()),
                       members.getOrDefault(creatorId, MemberSummary.deactivated(creatorId)),
                       members.getOrDefault(updaterId, MemberSummary.deactivated(updaterId)));
    }
}
