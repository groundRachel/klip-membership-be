package com.klipwallet.membership.dto.admin;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.dto.admin.AdminDto.Detail;
import com.klipwallet.membership.dto.admin.AdminDto.Row;
import com.klipwallet.membership.dto.datetime.DateTimeAssembler;
import com.klipwallet.membership.dto.member.MemberAssembler;
import com.klipwallet.membership.dto.member.MemberSummary;
import com.klipwallet.membership.entity.Admin;
import com.klipwallet.membership.entity.MemberId;

@Component
@RequiredArgsConstructor
public class AdminAssembler {
    private final MemberAssembler memberAssembler;
    private final DateTimeAssembler dtAssembler;

    public List<Row> toRows(List<Admin> admins) {
        return admins.stream()
                     .map(this::toRow)
                     .collect(Collectors.toList());
    }

    @SuppressWarnings("DataFlowIssue")
    private AdminDto.Row toRow(Admin entity) {
        Map<MemberId, MemberSummary> members = memberAssembler.getMemberSummaryMap(entity.getCreatorId());
        return new AdminDto.Row(entity.getMemberId(), entity.getEmail(), dtAssembler.toOffsetDateTime(entity.getCreatedAt()),
                                members.getOrDefault(entity.getCreatorId(), MemberSummary.deactivated(entity.getCreatorId())));
    }

    @SuppressWarnings("DataFlowIssue")
    public Detail toDetail(@NonNull Admin admin) {
        Map<MemberId, MemberSummary> members = memberAssembler.getMemberSummaryMapBy(admin);
        return new AdminDto.Detail(admin.getMemberId(), admin.getEmail(), admin.getName(), admin.getOAuthId(),
                                   dtAssembler.toOffsetDateTime(admin.getCreatedAt()),
                                   members.getOrDefault(admin.getCreatorId(), MemberSummary.deactivated(admin.getCreatorId())),
                                   dtAssembler.toOffsetDateTime(admin.getUpdatedAt()),
                                   members.getOrDefault(admin.getUpdaterId(), MemberSummary.deactivated(admin.getUpdaterId())));
    }
}
