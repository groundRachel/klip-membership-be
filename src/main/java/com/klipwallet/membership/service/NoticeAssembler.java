package com.klipwallet.membership.service;

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
import com.klipwallet.membership.dto.notice.NoticeDto.Detail;
import com.klipwallet.membership.dto.notice.NoticeDto.Row;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Notice;

import static java.util.stream.Collectors.toUnmodifiableSet;

@Component
@RequiredArgsConstructor
public class NoticeAssembler {
    private final DateTimeAssembler dtAssembler;
    private final MemberAssembler memberAssembler;

    @Nonnull
    public Detail toDetail(@NonNull Notice notice) {
        Map<MemberId, MemberSummary> members = memberAssembler.getMemberSummaryMap(notice.getAccessorIds());
        return new Detail(notice.getId(), notice.getTitle(), notice.getBody(), notice.isPrimary(), notice.getStatus(),
                          dtAssembler.toOffsetDateTime(notice.getLivedAt()),
                          dtAssembler.toOffsetDateTime(notice.getCreatedAt()),
                          dtAssembler.toOffsetDateTime(notice.getUpdatedAt()),
                          members.getOrDefault(notice.getCreatorId(), MemberSummary.deactivated(notice.getCreatorId())),
                          members.getOrDefault(notice.getUpdaterId(), MemberSummary.deactivated(notice.getUpdaterId())));
    }

    public List<Row> toRows(List<Notice> notices) {
        Map<MemberId, MemberSummary> members = getMemberSummaryMap(notices);
        return notices.stream()
                      .map(n -> toRow(n, members))
                      .collect(Collectors.toList());
    }

    private Map<MemberId, MemberSummary> getMemberSummaryMap(List<Notice> notices) {
        Set<MemberId> memberIds = toAccessorIds(notices);
        return memberAssembler.getMemberSummaryMap(memberIds);
    }

    private Set<MemberId> toAccessorIds(Collection<Notice> notices) {
        return notices.stream()
                      .map(Notice::getAccessorIds)
                      .flatMap(Set::stream)
                      .collect(toUnmodifiableSet());
    }

    private Row toRow(Notice entity, Map<MemberId, MemberSummary> members) {
        MemberId creatorId = entity.getCreatorId();
        MemberId updaterId = entity.getUpdaterId();
        return new Row(entity.getId(), entity.getTitle(), entity.isPrimary(),
                       dtAssembler.toOffsetDateTime(entity.getCreatedAt()),
                       members.getOrDefault(creatorId, MemberSummary.deactivated(creatorId)),
                       dtAssembler.toOffsetDateTime(entity.getUpdatedAt()),
                       members.getOrDefault(updaterId, MemberSummary.deactivated(updaterId)));
    }
}
