package com.klipwallet.membership.adaptor.klipdrops.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KlipDropsPartners(
        @JsonProperty("result") List<KlipDropsPartner> klipDropsPartners,
        @JsonProperty("next_cursor") String cursor
) {
}
