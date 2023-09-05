package com.klipwallet.membership.adaptor.klipdrops.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KlipDropsDrops(
        @JsonProperty("drops") List<KlipDropsDrop> drops,
        @JsonProperty("totalCount") Long totalCount
) {
}
