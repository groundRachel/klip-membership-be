package com.klipwallet.membership.adaptor.klipdrops.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KlipDropsPartner(
        @JsonProperty("business_registration_number") String businessRegistrationNumber,
        @JsonProperty("partner_id") Integer partnerId,
        @JsonProperty("name") String name,
        @JsonProperty("phone_number") String phoneNumber,
        @JsonProperty("status") String status,
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("updated_at") LocalDateTime updatedAt
) {
}
