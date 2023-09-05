package com.klipwallet.membership.adaptor.klipdrops.dto;

import java.math.BigInteger;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KlipDropsDrop(
        @JsonProperty("id") BigInteger id,
        @JsonProperty("nft_sca") String nftSmartContractAddress,
        @JsonProperty("title") String title,
        @JsonProperty("creator_name") String creatorName,
        @JsonProperty("total_supply") Integer totalSupply,
        @JsonProperty("total_sales_count") Integer totalSalesCount,
        @JsonProperty("status") DropStatus status,
        @JsonProperty("open_at") OffsetDateTime openAt,
        @JsonProperty("start_at") OffsetDateTime startAt
) {}
