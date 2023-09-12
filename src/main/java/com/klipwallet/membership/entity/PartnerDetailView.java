package com.klipwallet.membership.entity;

import java.time.LocalDateTime;

public interface PartnerDetailView {
    Integer getId();

    String getName();

    String getBusinessRegistrationNumber();

    String getEmail();

    LocalDateTime getCreatedAt();

    Integer getKlipDropsPartnerId();

    Integer getProcessorId();

    LocalDateTime getProcessedAt();
}
