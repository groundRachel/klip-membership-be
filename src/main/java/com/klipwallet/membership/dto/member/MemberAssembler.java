package com.klipwallet.membership.dto.member;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.klipwallet.membership.entity.AccessorIdsGettable;
import com.klipwallet.membership.entity.MemberId;

import static java.util.stream.Collectors.toUnmodifiableSet;

@Component
public class MemberAssembler {
    public MemberSummary getMemberSummary(MemberId id) {
        return MemberSummary.deactivated(id);
    }

    public Map<MemberId, MemberSummary> getMemberSummaryMap(@SuppressWarnings("unused") Collection<MemberId> ids) {
        // TODO @Jordan
        return Collections.emptyMap();
    }

    @SuppressWarnings("unused")
    public Map<MemberId, MemberSummary> getMemberSummaryMap(MemberId... ids) {
        // TODO @Jordan
        return Collections.emptyMap();
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
