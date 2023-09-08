package com.klipwallet.membership.dto.openchatting;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.dto.datetime.DateTimeAssembler;
import com.klipwallet.membership.entity.OpenChatting;

@Component
@RequiredArgsConstructor
public class OpenChattingAssembler {
    private final DateTimeAssembler dtAssembler;

    public List<OpenChattingRow> toRows(List<OpenChatting> entities) {
        return entities.stream()
                       .map(this::toRow)
                       .toList();
    }

    private OpenChattingRow toRow(OpenChatting entity) {
        return new OpenChattingRow(entity.getId(), entity.getTitle(), entity.getContractAddress(), entity.getStatus(),
                                   entity.getSource(), entity.getCreatorId().value(), entity.getUpdaterId().value(),
                                   dtAssembler.toOffsetDateTime(entity.getCreatedAt()),
                                   dtAssembler.toOffsetDateTime(entity.getUpdatedAt()));
    }

    public OpenChattingSummary toSummary(OpenChatting entity) {
        return new OpenChattingSummary(entity.getId(), entity.getKakaoOpenlinkSummary().getId(), entity.getKakaoOpenlinkSummary().getUrl(),
                                       entity.getTitle(), entity.getCreatorId().value(),
                                       dtAssembler.toOffsetDateTime(entity.getCreatedAt()));
    }
}
