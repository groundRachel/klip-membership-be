package com.klipwallet.membership.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.klipwallet.membership.dto.member.MemberSummary;
import com.klipwallet.membership.entity.MemberId;

@SuppressWarnings("unused")
@Component
public class MemberAssembler {
    public MemberSummary getMemberSummary(MemberId id) {
        return MemberSummary.deactivated(id);
    }

    public Map<MemberId, MemberSummary> getMemberSummaryMap(Collection<MemberId> ids) {
        // TODO @Jordan
        return Collections.emptyMap();
    }

    public Map<MemberId, MemberSummary> getMemberSummaryMap(MemberId... ids) {
        // TODO @Jordan
        return Collections.emptyMap();
    }
}
