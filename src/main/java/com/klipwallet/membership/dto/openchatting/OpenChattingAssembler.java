package com.klipwallet.membership.dto.openchatting;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.dto.datetime.DateTimeAssembler;
import com.klipwallet.membership.entity.OpenChatting;

@Component
@RequiredArgsConstructor
public class OpenChattingAssembler {
    private final DateTimeAssembler dtAssembler;

    public Page<OpenChattingSummary> toSummaries(Page<OpenChatting> page) {
        return page.map(this::toSummary);
    }

    private OpenChattingSummary toSummary(OpenChatting entity) {
        LocalDateTime deletedAt = null;
        if (entity.isDeleted()) {
            deletedAt = entity.getUpdatedAt();
        }
        return new OpenChattingSummary(entity.getId(), entity.getKakaoOpenlinkSummary().getId(), entity.getKakaoOpenlinkSummary().getUrl(),
                                       entity.getTitle(), entity.getStatus(),
                                       dtAssembler.toOffsetDateTime(entity.getCreatedAt()), dtAssembler.toOffsetDateTime(deletedAt)
        );
    }
}
