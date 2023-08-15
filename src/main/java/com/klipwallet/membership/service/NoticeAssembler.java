package com.klipwallet.membership.service;

import java.util.Map;

import jakarta.annotation.Nonnull;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.dto.datetime.DateTimeAssembler;
import com.klipwallet.membership.dto.member.MemberSummary;
import com.klipwallet.membership.dto.notice.NoticeDto.Detail;
import com.klipwallet.membership.entity.BaseMeta;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Notice;

@Component
@RequiredArgsConstructor
public class NoticeAssembler {
    private final DateTimeAssembler dateTimeAssembler;
    private final MemberAssembler memberAssembler;

    @Nonnull
    public Detail toDetail(@NonNull Notice notice) {
        BaseMeta base = notice.getBase();
        Map<MemberId, MemberSummary> members = memberAssembler.getMemberSummaryMap(base.getCreatedBy(), base.getUpdatedBy());
        return new Detail(notice.getId(), notice.getTitle(), notice.getBody(), notice.isMain(),
                          dateTimeAssembler.toOffsetDateTime(base.getCreatedAt()),
                          dateTimeAssembler.toOffsetDateTime(base.getUpdatedAt()),
                          members.getOrDefault(base.getCreatedBy(), MemberSummary.deactivated(base.getCreatedBy())),
                          members.getOrDefault(base.getUpdatedBy(), MemberSummary.deactivated(base.getUpdatedBy())));
    }
}
