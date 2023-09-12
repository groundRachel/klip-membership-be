package com.klipwallet.membership.entity;

import java.time.LocalDateTime;

public interface PartnerSummaryView {
    Integer getMemberId();

    String getName();

    LocalDateTime getProcessedAt();

    Integer getProcessorId();

    // TODO 오픈채팅 운영 여부 정보 포함하기
}
