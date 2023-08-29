package com.klipwallet.membership.adaptor.klipdrops.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Drop(
        @JsonProperty("id") Long id,
        @JsonProperty("title") String title,
        @JsonProperty("creator_name") String creatorName,
        @JsonProperty("total_supply") Integer totalSupply,
        @JsonProperty("total_sales_count") Integer totalSalesCount,
        @JsonProperty("status") String status,
        @JsonProperty("open_at") String openAt,
        @JsonProperty("start_at") String startAt
) {
}
