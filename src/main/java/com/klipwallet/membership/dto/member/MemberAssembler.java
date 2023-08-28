package com.klipwallet.membership.dto.member;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.entity.AccessorIdsGettable;
import com.klipwallet.membership.entity.Member;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.repository.MemberRepository;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableSet;

@Component
@RequiredArgsConstructor
public class MemberAssembler {
    private final MemberRepository memberRepository;

    public MemberSummary getMemberSummary(MemberId memberId) {
        return memberRepository.findById(memberId.value())
                               .map(MemberSummary::new)
                               .orElseGet(() -> MemberSummary.deactivated(memberId));
    }

    public String toMemberName(MemberId memberId) {
        if (memberId == null) {
            return null;
        }
        return getMemberSummary(memberId).name();
    }

    public Map<MemberId, MemberSummary> getMemberSummaryMap(Collection<MemberId> memberIds) {
        Set<Integer> ids = toIdSet(memberIds);
        List<Member> members = memberRepository.findAllById(ids);
        return members.stream()
                      .collect(toMap(Member::getMemberId, MemberSummary::new));
    }

    @NonNull
    private Set<Integer> toIdSet(Collection<MemberId> memberIds) {
        return memberIds.stream()
                        .map(MemberId::value)
                        .collect(Collectors.toSet());
    }

    @SuppressWarnings("unused")
    public Map<MemberId, MemberSummary> getMemberSummaryMap(MemberId... memberIds) {
        return getMemberSummaryMap(List.of(memberIds));
    }

    public Map<MemberId, MemberSummary> getMemberSummaryMapBy(AccessorIdsGettable entity) {
        return getMemberSummaryMap(entity.getAccessorIds());
    }

    public Map<MemberId, MemberSummary> getMemberSummaryMapBy(Collection<? extends AccessorIdsGettable> entities) {
        Set<MemberId> memberIds = toAccessorIds(entities);
        return getMemberSummaryMap(memberIds);
    }

    private Set<MemberId> toAccessorIds(Collection<? extends AccessorIdsGettable> notices) {
        return notices.stream()
                      .map(AccessorIdsGettable::getAccessorIds)
                      .flatMap(Collection::stream)
                      .collect(toUnmodifiableSet());
    }
}
